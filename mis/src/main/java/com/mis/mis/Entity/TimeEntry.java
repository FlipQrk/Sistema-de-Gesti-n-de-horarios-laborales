package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

/**
 * ENTIDAD: TimeEntry (Registro de Entrada/Salida)
 * 
 * Registra la entrada y salida diaria de un empleado.
 * Fundamental para el control de asistencia y cálculo de horas trabajadas.
 * 
 * Tabla: time_entries
 * Relaciones:
 *   - * Registros → 1 Empleado (ManyToOne)
 * 
 * Índices para optimización:
 *   - idx_employee_date: Para buscar registros de un empleado en una fecha
 *   - idx_entry_date: Para búsquedas generales por fecha
 * 
 * Ejemplo:
 *   Juan García → 01/03/2026 → Check-in: 06:15 → Check-out: 14:30 → Horas worked: 8.25
 * 
 * Funcionalidades:
 *   - Cálculo automático de horas trabajadas
 *   - Detección de retrasos
 *   - Detección de salida temprana
 *   - Cálculo de horas extra
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "time_entries", indexes = {
        @Index(name = "idx_employee_date", columnList = "employee_id, entry_date"),  // Búsqueda por empleado y fecha
        @Index(name = "idx_entry_date", columnList = "entry_date")  // Búsqueda por fecha
})
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class TimeEntry {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único del registro de entrada/salida (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Empleado que realiza la entrada/salida
     * Relación ManyToOne: múltiples registros → un empleado
     * 
     * FetchType.LAZY: No carga el empleado automáticamente (mejor rendimiento)
     * @JoinColumn: Especifica que employee_id es la clave foránea
     */
    @NotNull(message = "El empleado es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * Fecha del registro de entrada/salida
     * Una entrada y una salida por día por empleado
     * Ejemplo: 01/03/2026
     */
    @NotNull(message = "La fecha de entrada es requerida")
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    /**
     * Hora exacta de entrada del empleado
     * Registrada automáticamente al hacer check-in
     * Ejemplo: 06:15 (6:15 AM)
     */
    @NotNull(message = "La hora de entrada es requerida")
    @Column(name = "check_in_time", nullable = false)
    private LocalTime checkInTime;

    /**
     * Hora exacta de salida del empleado
     * Registrada automáticamente al hacer check-out
     * Campo opcional: nullable hasta que se cierre la jornada
     * Ejemplo: 14:30 (2:30 PM)
     */
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    /**
     * Total de horas trabajadas en el día
     * CALCULADO AUTOMÁTICAMENTE como (checkOutTime - checkInTime)
     * Se actualiza en @PrePersist y @PreUpdate via calculateHoursWorked()
     * Validación: debe ser mayor a 0
     * Ejemplo: 8.25 horas
     */
    @DecimalMin(value = "0.0", message = "Las horas no pueden ser negativas")
    @Column(name = "hours_worked")
    private Double hoursWorked;

    /**
     * Notas o comentarios adicionales sobre el registro
     * Ej: "Retardo justificado", "Salida temprana autorizada", "Defecto en sistema"
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Estado actual del registro de entrada/salida
     * Valores: CHECKED_IN (dentro), CHECKED_OUT (salió), ABSENT (ausente), LATE (retardo),
     *          EARLY_LEAVE (salida temprana), APPROVED (aprobado), PENDING_APPROVAL (pendiente)
     * Por defecto: CHECKED_IN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EntryStatus status = EntryStatus.CHECKED_IN;

    /**
     * Indicador de hora extra
     * true: Si hoursWorked excede las horas esperadas del turno
     * false: Si hoursWorked es normal
     * Se calcula automáticamente en la lógica de negocio
     * Por defecto: false
     */
    @Column(name = "is_overtime", nullable = false)
    @Builder.Default
    private Boolean isOvertime = false;

    /**
     * Timestamp de creación del registro
     * Se asigna automáticamente en @PrePersist
     * No se puede actualizar una vez creado
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp de última actualización
     * Se actualiza automáticamente en cada @PreUpdate
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== MÉTODOS DE CICLO DE VIDA ====================
    
    /**
     * Método automático ejecutado ANTES de insertar un registro en BD
     * Asigna la fecha y hora actual a createdAt y updatedAt
     * Calcula automáticamente las horas trabajadas si hay check-out
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Calcula horas trabajadas si ya hay check-out
        if (this.checkOutTime != null && this.checkInTime != null) {
            calculateHoursWorked();
        }
    }

    /**
     * Método automático ejecutado ANTES de actualizar un registro en BD
     * Actualiza el timestamp de updatedAt a la hora actual
     * Recalcula las horas trabajadas si cambió el check-out
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Recalcula horas trabajadas si hay check-out
        if (this.checkOutTime != null && this.checkInTime != null) {
            calculateHoursWorked();
        }
    }

    /**
     * Calcula automáticamente las horas trabajadas
     * Resta checkOutTime - checkInTime
     * Maneja cambios de día (ej: turno nocturno 22:00 a 06:00)
     * Resultado: horas con decimales (8.5 = 8 horas 30 minutos)
     */
    private void calculateHoursWorked() {
        Duration duration = Duration.between(this.checkInTime, this.checkOutTime);
        this.hoursWorked = duration.toMinutes() / 60.0;
    }

    // ==================== ENUMS ====================
    
    /**
     * Enum que define los estados posibles de un registro de entrada/salida
     * 
     * CHECKED_IN: Empleado registró entrada pero no ha salido
     * CHECKED_OUT: Empleado registró entrada y salida completa
     * ABSENT: Empleado marcado como ausente
     * LATE: Empleado entró después de la hora permitida
     * EARLY_LEAVE: Empleado salió antes de lo permitido
     * APPROVED: Registro aprobado por gerencia
     * PENDING_APPROVAL: Registro en espera de aprobación
     */
    public enum EntryStatus {
        CHECKED_IN, CHECKED_OUT, ABSENT, LATE, EARLY_LEAVE, APPROVED, PENDING_APPROVAL
    }
}
