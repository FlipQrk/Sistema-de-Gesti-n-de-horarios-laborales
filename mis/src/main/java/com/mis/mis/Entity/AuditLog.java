package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ENTIDAD: AuditLog (Registro de Auditoría)
 * 
 * Registra todas las acciones y cambios realizados en el sistema.
 * Proporciona trazabilidad completa para cumplimiento normativo y seguridad.
 * 
 * Tabla: audit_logs
 * No tiene relaciones directas (es independiente)
 * 
 * Registra:
 *   - Creación, actualización, eliminación de empleados
 *   - Creación, actualización de horarios
 *   - Registros de entrada/salida
 *   - Aprobaciones y rechazos
 *   - Accesos al sistema
 * 
 * Índices para optimización:
 *   - idx_entity_type: Para buscar cambios de un tipo de entidad
 *   - idx_changed_by: Para buscar cambios realizados por un usuario
 *   - idx_changed_at: Para búsquedas por período de tiempo
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_entity_type", columnList = "entity_type"),  // Búsqueda por tipo de entidad
        @Index(name = "idx_changed_by", columnList = "changed_by"),    // Búsqueda por usuario
        @Index(name = "idx_changed_at", columnList = "changed_at")     // Búsqueda por fecha
})
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class AuditLog {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único del registro de auditoría (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de entidad que fue modificada
     * Ej: "Employee", "WorkSchedule", "TimeEntry", "Department"
     * Validación: obligatorio
     */
    @NotBlank(message = "El tipo de entidad es requerido")
    @Column(nullable = false)
    private String entityType;

    /**
     * ID específico del registro que fue modificado
     * Se usa para rastrear qué empleado, horario, etc. fue afectado
     * Ejemplo: Si entityType="Employee" → entityId=5 (empleado con ID 5)
     * Validación: obligatorio
     */
    @NotNull(message = "El ID de la entidad es requerido")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    /**
     * Acción realizada sobre la entidad
     * Valores: CREATE (creación), UPDATE (actualización), DELETE (eliminación),
     *          VIEW (acceso), APPROVE (aprobación), REJECT (rechazo)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    /**
     * Usuario o sistema que realizó el cambio
     * Ej: "admin@empresa.com", "SYSTEM", "juan.garcia@empresa.com"
     * Se obtiene del contexto de seguridad de Spring Security
     * Validación: obligatorio
     */
    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    /**
     * Valor anterior de la entidad (antes del cambio)
     * Útil para comparar qué cambió exactamente
     * Formato: JSON o String serializado
     * Campo opcional: null para CREATE actions
     */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /**
     * Valor nuevo de la entidad (después del cambio)
     * Útil para comparar qué cambió exactamente
     * Formato: JSON o String serializado
     * Campo opcional: null para DELETE actions
     */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * Fecha y hora exacta en que se realizó el cambio
     * Se asigna automáticamente en @PrePersist
     * Formato: LocalDateTime (YYYY-MM-DD HH:mm:ss)
     * No se puede actualizar
     */
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    /**
     * Descripción adicional del cambio
     * Ej: "Cambio de departamento RH a IT", "Aprobación de horario quincenal"
     * Campo opcional para notas contextuales
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    // ==================== MÉTODOS DE CICLO DE VIDA ====================
    
    /**
     * Método automático ejecutado ANTES de insertar un registro en BD
     * Asigna la fecha y hora actual a changedAt
     * El audit log siempre registra el momento exacto de la acción
     */
    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }

    // ==================== ENUMS ====================
    
    /**
     * Enum que define los tipos de acciones auditables
     * 
     * CREATE: Creación de un nuevo registro
     * UPDATE: Actualización de un registro existente
     * DELETE: Eliminación de un registro
     * VIEW: Acceso o visualización de datos (opcional)
     * APPROVE: Aprobación de un registro (ej: horario)
     * REJECT: Rechazo de un registro
     */
    public enum AuditAction {
        CREATE, UPDATE, DELETE, VIEW, APPROVE, REJECT
    }
}
