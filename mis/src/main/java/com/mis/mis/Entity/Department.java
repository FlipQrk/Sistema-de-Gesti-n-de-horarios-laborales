package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD: Department (Departamento)
 * 
 * Representa los departamentos de la organización.
 * Cada departamento puede contener múltiples empleados.
 * 
 * Tabla: departments
 * Relaciones:
 *   - 1 Departamento → * Empleados (OneToMany)
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "departments")
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class Department {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único del departamento (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del departamento (ej: "Recursos Humanos")
     * Validación: no puede estar vacío
     * Característica: debe ser único en la base de datos
     */
    @NotBlank(message = "El nombre del departamento no puede estar vacío")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Descripción detallada del departamento
     * Campo opcional para agregar información adicional
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Código abreviado del departamento (ej: "RH", "IT", "VENTAS")
     * Validación: requerido y único
     * Utilizado para referencias rápidas en reportes
     */
    @NotBlank(message = "El código del departamento es requerido")
    @Column(unique = true, nullable = false)
    private String code;

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

    // ==================== RELACIONES ====================
    
    /**
     * Lista de empleados que pertenecen a este departamento
     * Relación OneToMany mapeada desde Employee
     * 
     * CascadeType.ALL: Si se elimina el departamento, se eliminan los empleados
     * orphanRemoval: Los empleados sin departamento se eliminan de la BD
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude  // Excluye esta colección de toString() para evitar referencias circulares
    private List<Employee> employees = new ArrayList<>();

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
}
