package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD: ScheduleTemplate (Plantilla de Horarios)
 * 
 * Define plantillas reutilizables de horarios.
 * Permite asignar múltiples turnos en forma automática.
 * Útil para patrones recurrentes (semanal, quincenal, etc.)
 * 
 * Tabla: schedule_templates
 * Relaciones:
 *   - * Plantillas ↔ * Turnos (ManyToMany a través de template_shifts)
 * 
 * Ejemplo de plantilla:
 *   "Rotación Semanal 3 Turnos"
 *   Turnos incluidos:
 *     - Lunes-Miércoles: Turno Mañana (6:00-14:00)
 *     - Jueves-Viernes: Turno Tarde (14:00-22:00)
 *     - Fin de semana: Descanso
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "schedule_templates")
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class ScheduleTemplate {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único de la plantilla (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único de la plantilla
     * Ej: "Rotación 3 Turnos", "Turno Estándar 8 horas", "Fin de semana flexible"
     * Validación: obligatorio, único y no puede estar vacío
     */
    @NotBlank(message = "El nombre de la plantilla es requerido")
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Descripción detallada de la plantilla
     * Explica el patrón de turnos y cómo se usa
     * Ej: "Lunes a viernes turno mañana, sábado tarde, domingo libre"
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Turnos que forman parte de esta plantilla
     * Relación ManyToMany: una plantilla puede tener múltiples turnos
     *                      un turno puede pertenecer a múltiples plantillas
     * 
     * @NotNull: Debe haber al menos un turno
     * @JoinTable: Crea tabla intermedia template_shifts
     * FetchType.LAZY: No carga turnos automáticamente (mejor rendimiento)
     */
    @NotNull(message = "Debe especificar al menos un turno")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "template_shifts",  // Tabla que une plantillas con turnos
            joinColumns = @JoinColumn(name = "template_id"),  // Columna de plantillas
            inverseJoinColumns = @JoinColumn(name = "shift_id")  // Columna de turnos
    )
    private List<Shift> shifts = new ArrayList<>();

    /**
     * Frecuencia de repetición en días
     * 7 = patrón semanal
     * 14 = patrón quincenal
     * 30 = patrón mensual
     * null = sin repetición automática
     * 
     * Permite reutilizar automáticamente patrones recurrentes
     */
    @Column(name = "repeat_frequency")
    private Integer repeatFrequency;

    /**
     * Estado de la plantilla
     * Valores: ACTIVE (activa), INACTIVE (inactiva), ARCHIVED (archivada)
     * Por defecto: ACTIVE
     * Las plantillas inactivas no se pueden asignar a empleados
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TemplateStatus status = TemplateStatus.ACTIVE;

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
     * Enum que define los estados posibles de una plantilla de horarios
     * 
     * ACTIVE: Plantilla activa y disponible para asignar
     * INACTIVE: Plantilla inactiva (no se puede asignar)
     * ARCHIVED: Plantilla archivada (históricamente) y no se puede asignar
     */
    public enum TemplateStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }
}
