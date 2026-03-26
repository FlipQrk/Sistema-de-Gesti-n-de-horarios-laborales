package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * ENTIDAD: WorkSchedule (Horario de Trabajo)
 * 
 * Asignación de un turno a un empleado para un período específico.
 * Representa el horario laboral esperado de un empleado.
 * 
 * Tabla: work_schedules
 * Relaciones:
 *   - * Horarios → 1 Empleado (ManyToOne)
 *   - * Horarios → 1 Turno (ManyToOne)
 * 
 * Índices para optimización:
 *   - idx_employee_id: Para buscar horarios de un empleado rápidamente
 *   - idx_start_date_end_date: Para buscar horarios en un período de fechas
 * 
 * Ejemplo:
 *   Juan García → Turno Mañana (6:00-14:00) → Del 01/03/2026 al 31/03/2026
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "work_schedules", indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id"),  // Búsqueda por empleado
        @Index(name = "idx_start_date_end_date", columnList = "start_date, end_date")  // Búsqueda por período
})
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class WorkSchedule {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único del horario de trabajo (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha de inicio de vigencia del horario
     * A partir de esta fecha el empleado tiene el turno asignado
     * Ejemplo: 01/03/2026
     */
    @NotNull(message = "La fecha de inicio es requerida")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Fecha de finalización de vigencia del horario
     * Hasta esta fecha el empleado tiene el turno asignado
     * Ejemplo: 31/03/2026
     */
    @NotNull(message = "La fecha de fin es requerida")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Empleado a quien se asigna este horario
     * Relación ManyToOne: múltiples horarios → un empleado
     * 
     * FetchType.LAZY: No carga el empleado automáticamente (mejor rendimiento)
     * @JoinColumn: Especifica que employee_id es la clave foránea
     */
    @NotNull(message = "El empleado es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * Turno asignado al empleado en este período
     * Relación ManyToOne: múltiples horarios → un turno
     * Determina las horas de trabajo (startTime, endTime)
     * 
     * FetchType.LAZY: No carga el turno automáticamente (mejor rendimiento)
     * @JoinColumn: Especifica que shift_id es la clave foránea
     */
    @NotNull(message = "El turno es requerido")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    /**
     * Estado del horario de trabajo
     * Valores: ACTIVE (activo), INACTIVE (inactivo), PENDING_APPROVAL (pendiente aprobación), REJECTED (rechazado)
     * Por defecto: ACTIVE
     * 
     * Permite workflow de aprobación de horarios
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.ACTIVE;

    /**
     * Notas adicionales sobre el horario
     * Ej: "Turno especial", "Cambio temporal", "Por aprobar jefe directo"
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

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
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Método automático ejecutado ANTES de actualizar un registro en BD
     * Actualiza el timestamp de updatedAt a la hora actual
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== ENUMS ====================
    
    /**
     * Enum que define los estados posibles de un horario de trabajo
     * 
     * ACTIVE: Horario activo y vigente
     * INACTIVE: Horario inactivo o descontinuado
     * PENDING_APPROVAL: Horario en espera de aprobación del gerente
     * REJECTED: Horario rechazado o no aprobado
     */
    public enum ScheduleStatus {
        ACTIVE, INACTIVE, PENDING_APPROVAL, REJECTED
    }
}
