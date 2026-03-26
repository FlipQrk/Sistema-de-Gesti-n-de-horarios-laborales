package com.mis.mis.Entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD: Employee (Empleado)
 * 
 * Representa a los empleados de la organización.
 * Contiene información personal y laboral.
 * 
 * Tabla: employees
 * Relaciones:
 *   - * Empleados → 1 Departamento (ManyToOne)
 *   - 1 Empleado → * Horarios de Trabajo (OneToMany)
 *   - 1 Empleado → * Registros de Entrada/Salida (OneToMany)
 * 
 * Restricciones Únicas:
 *   - email debe ser único
 *   - identification_number debe ser único
 * 
 * @author Sistema de Horarios Laborales
 * @version 1.0
 */
@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),  // Email único a nivel de BD
        @UniqueConstraint(columnNames = "identification_number")  // Cédula/DNI único
})
@Data                    // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor      // Lombok: constructor sin argumentos (requerido por JPA)
@AllArgsConstructor     // Lombok: constructor con todos los argumentos
@Builder               // Lombok: patrón builder para crear instancias
public class Employee {

    // ==================== CAMPOS PRINCIPALES ====================
    
    /**
     * ID único del empleado (clave primaria)
     * Generado automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del empleado (ej: "Juan")
     * Validación: obligatorio y no puede estar vacío
     */
    @NotBlank(message = "El nombre es requerido")
    @Column(nullable = false)
    private String firstName;

    /**
     * Apellido del empleado (ej: "García")
     * Validación: obligatorio y no puede estar vacío
     */
    @NotBlank(message = "El apellido es requerido")
    @Column(nullable = false)
    private String lastName;

    /**
     * Correo electrónico del empleado
     * Validación: formato de email válido, único en la base de datos
     * Usado para comunicaciones y autenticación
     */
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es requerido")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Número de identificación del empleado (Cédula, DNI, Pasaporte, etc.)
     * Validación: obligatorio y único
     * Campo crítico para auditoría y nómina
     */
    @NotBlank(message = "El número de identificación es requerido")
    @Column(nullable = false, unique = true)
    private String identificationNumber;

    /**
     * Fecha de contratación del empleado
     * Utilizado para calcular antigüedad y beneficios
     */
    @NotNull(message = "La fecha de contratación es requerida")
    @Column(nullable = false)
    private LocalDate hireDate;

    /**
     * Número de teléfono del empleado
     * Campo opcional para contacto directo
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Puesto o cargo del empleado (ej: "Ingeniero de Sistemas", "Contador")
     * Campo opcional para descripción del rol
     */
    @Column(columnDefinition = "TEXT")
    private String position;

    /**
     * Estado actual del empleado
     * Valores: ACTIVE (activo), INACTIVE (inactivo), ON_LEAVE (licencia), TERMINATED (despedido)
     * Por defecto: ACTIVE
     */
    @Enumerated(EnumType.STRING)  // Almacena el nombre del enum como STRING en la BD
    @Column(nullable = false)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

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
     * Departamento al que pertenece el empleado
     * Relación ManyToOne: múltiples empleados → un departamento
     * 
     * FetchType.LAZY: No carga el departamento automáticamente (mejor rendimiento)
     * @JoinColumn: Especifica la columna de clave foránea en la tabla employees
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /**
     * Lista de horarios de trabajo asignados a este empleado
     * Relación OneToMany mapeada desde WorkSchedule
     * 
     * CascadeType.ALL: Si se elimina el empleado, se eliminan sus horarios
     * orphanRemoval: Los horarios huérfanos se eliminan automáticamente
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude  // Excluye de toString() para evitar referencias circulares
    private List<WorkSchedule> workSchedules = new ArrayList<>();

    /**
     * Lista de registros de entrada/salida del empleado
     * Relación OneToMany mapeada desde TimeEntry
     * Contiene el histórico de asistencia y puntualidad
     * 
     * CascadeType.ALL: Si se elimina el empleado, se eliminan sus registros
     * orphanRemoval: Los registros huérfanos se eliminan automáticamente
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude  // Excluye de toString() para evitar referencias circulares
    private List<TimeEntry> timeEntries = new ArrayList<>();

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
     * Enum que define los estados posibles de un empleado
     * 
     * ACTIVE: Empleado activo y trabajando
     * INACTIVE: Empleado inactivo (sin asignaciones)
     * ON_LEAVE: Empleado en licencia (vacaciones, enfermedad, etc.)
     * TERMINATED: Empleado despedido o renunció
     */
    public enum EmployeeStatus {
        ACTIVE, INACTIVE, ON_LEAVE, TERMINATED
    }
}
