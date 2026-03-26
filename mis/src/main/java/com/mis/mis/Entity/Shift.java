package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * ENTIDAD: Shift (Turno)
 * 
 * Representa los turnos laborales de la organización.
 * Define horarios estándar que se asignan a los empleados.
 * 
 * Ejemplos de turnos:
 *   - Turno Mañana: 6:00 AM - 2:00 PM
 *   - Turno Tarde: 2:00 PM - 10:00 PM
 *   - Turno Noche: 10:00 PM - 6:00 AM
 * 
 * Tabla: shifts
 * Relaciones:
 *   - * Turnos → * Templates (ManyToMany a través de ScheduleTemplate)
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "shifts")
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class Shift {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único del turno (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del turno (ej: "Turno Mañana", "Turno Noche")
     * Validación: obligatorio y no puede estar vacío
     */
    @NotBlank(message = "El nombre del turno es requerido")
    @Column(nullable = false)
    private String name;

    /**
     * Hora de inicio del turno (ej: 06:00 para 6:00 AM)
     * Validación: obligatorio
     * Formato: LocalTime (HH:mm:ss)
     */
    @NotNull(message = "La hora de inicio es requerida")
    @Column(nullable = false)
    private LocalTime startTime;

    /**
     * Hora de finalización del turno (ej: 14:00 para 2:00 PM)
     * Validación: obligatorio
     * Formato: LocalTime (HH:mm:ss)
     */
    @NotNull(message = "La hora de fin es requerida")
    @Column(nullable = false)
    private LocalTime endTime;

    /**
     * Descripción detallada del turno
     * Campo opcional para información adicional
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Total de horas de trabajo en este turno
     * Calculado automáticamente como (endTime - startTime)
     * Validación: debe estar entre 0 y 24 horas
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Las horas de trabajo deben ser mayores a 0")
    @DecimalMax(value = "24.0", inclusive = false, message = "Las horas de trabajo no pueden exceder 24")
    @Column(name = "work_hours")
    private Double workHours;

    /**
     * Código de color hexadecimal para identificar visualmente el turno en la UI
     * Ej: "#FF5733" (rojo), "#33FF57" (verde), "#3357FF" (azul)
     * Útil para calendarios y gráficos
     */
    @Column(name = "color_code")
    private String colorCode;

    /**
     * Tipo de turno para clasificación
     * Valores: MORNING (mañana), AFTERNOON (tarde), NIGHT (noche), STANDARD (estándar), CUSTOM (personalizado)
     * Por defecto: STANDARD
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ShiftType type = ShiftType.STANDARD;

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
     * Enum que define los tipos de turnos disponibles
     * 
     * MORNING: Turno matutino (típicamente 6:00 AM - 2:00 PM)
     * AFTERNOON: Turno vespertino (típicamente 2:00 PM - 10:00 PM)
     * NIGHT: Turno nocturno (típicamente 10:00 PM - 6:00 AM)
     * STANDARD: Turno estándar (8 horas regulares)
     * CUSTOM: Turno personalizado (definido según necesidades)
     */
    public enum ShiftType {
        MORNING, AFTERNOON, NIGHT, STANDARD, CUSTOM
    }
}
