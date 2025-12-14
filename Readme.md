# 🚀 Proyecto: Gestión de Persistencia con Spring Data JPA

Este proyecto implementa tres módulos CRUD (Crear, Leer, Actualizar, Borrar) completos utilizando **Spring Boot**, **Spring Data JPA** (Hibernate ORM) y **Thymeleaf** para la gestión de la persistencia de las entidades clave del sistema: `CodPos`, `Usuario` y `Direccion`.

## Objetivos del Proyecto

El objetivo principal es demostrar la competencia en la gestión de la persistencia de datos mediante el uso de herramientas ORM, desarrollando interfaces de administración funcionales y robustas para las entidades.

---

## 1. Módulo CRUD: Códigos Postales (`CodPos`)

Implementación completa de la gestión administrativa de los códigos postales, incluyendo su relación con la entidad `Provincia`.

### Requisitos Funcionales
* Listado, Alta, Edición y Eliminación de códigos postales.
* **Formularios:** Incluyen campos `cp`, `municipio`, y un **desplegable de `Provincia`** para enlazar el código postal con la provincia correspondiente.

### Endpoints del Módulo
| Verbo HTTP | Ruta | Acción |
| :--- | :--- | :--- |
| `GET` | `/admin/codpos` | Listar todos los códigos postales. |
| `GET` | `/admin/codpos/add` | Mostrar formulario de creación. |
| `POST` | `/admin/codpos/add` | Insertar nuevo código postal. |
| `GET` | `/admin/codpos/edit/{cp}` | Mostrar formulario de edición. |
| `POST` | `/admin/codpos/edit/{cp}` | Guardar cambios del código postal. |
| `GET` | `/admin/codpos/del/{cp}` | Mostrar formulario de confirmación de borrado. |
| `POST` | `/admin/codpos/del/{cp}` | Borrar el código postal. |

---

## 2. Módulo CRUD: Usuarios (`Usuario`)

Implementación completa de la gestión administrativa de los usuarios del sistema.

### Requisitos Funcionales
* Listado, Alta, Edición y Eliminación de usuarios.
* **Campos gestionados:** `username`, `email`, `nif`, `telefono`, `rol` (mediante desplegable con `ENUM`), `enabled` (activo/inactivo) y `password`.

### Regla Especial: Gestión de Contraseña
* Se utiliza un `PasswordEncoder` de tipo **`BCryptPasswordEncoder`**.
* **En la creación:** La contraseña debe ser cifrada con BCrypt.
* **En la edición:** Si el campo `password` llega **vacío**, la contraseña existente **no se modifica**. Si llega con valor, se debe cifrar con BCrypt antes de guardar.

### Endpoints del Módulo
| Verbo HTTP | Ruta | Acción |
| :--- | :--- | :--- |
| `GET` | `/admin/usuario` | Listar todos los usuarios. |
| `GET` | `/admin/usuario/add` | Mostrar formulario para crear usuario. |
| `POST` | `/admin/usuario/add` | Crear usuario (cifrado BCrypt). |
| `GET` | `/admin/usuario/edit/{id}` | Mostrar formulario para editar usuario. |
| `POST` | `/admin/usuario/edit/{id}` | Guardar cambios de usuario (manejo condicional de `password`). |
| `GET` | `/admin/usuario/del/{id}` | Mostrar formulario de confirmación de borrado. |
| `POST` | `/admin/usuario/del/{id}` | Borrar el usuario. |

---

## 3. Módulo CRUD: Direcciones (`Direccion`) - Maestro-Detalle

Implementación de la gestión de direcciones bajo el patrón **Maestro-Detalle**, donde el `Usuario` es el maestro y la `Direccion` es el detalle. Todas las direcciones están anidadas bajo un usuario específico.

### Requisitos Funcionales
* Para un usuario concreto (`{idUsuario}`): Listar, Crear, Editar y Eliminar sus direcciones.
* **Formularios de Dirección:** Incluyen `tipo_via`, `nombre_via`, `numero`, y un **desplegable de `CodPos`** (mostrando CP y municipio).
* El `Usuario` se toma del `{idUsuario}` en la ruta (patrón maestro–detalle).
* La vista de listado de usuarios debe incluir un enlace de acceso a la gestión de direcciones (`"Ver direcciones"`).

### Endpoints del Módulo (Maestro-Detalle)
| Verbo HTTP | Ruta | Acción |
| :--- | :--- | :--- |
| `GET` | `/admin/usuario/{idUsuario}/direccion` | Listar todas las direcciones del usuario. |
| `GET` | `/admin/usuario/{idUsuario}/direccion/add` | Mostrar formulario para crear nueva dirección. |
| `POST` | `/admin/usuario/{idUsuario}/direccion/add` | Crear dirección asociada al usuario. |
| `GET` | `/admin/usuario/{idUsuario}/direccion/edit/{idDireccion}` | Mostrar formulario para editar una dirección específica. |
| `POST` | `/admin/usuario/{idUsuario}/direccion/edit/{idDireccion}` | Guardar cambios de la dirección. |
| `GET` | `/admin/usuario/{idUsuario}/direccion/del/{idDireccion}` | Mostrar confirmación de borrado de dirección. |
| `POST` | `/admin/usuario/{idUsuario}/direccion/del/{idDireccion}` | Borrar la dirección. |

---

## ⚙️ Consideraciones Técnicas
* **Persistencia:** Uso de **Spring Data JPA** para los repositorios de las entidades.
* **Relaciones:** Correcta gestión de las relaciones ORM (ej. Maestro-Detalle entre `Usuario` y `Direccion`).
* **Vistas:** Utilización de **Thymeleaf** para las vistas y formularios, asegurando el correcto *binding* con los modelos.
* **Datos en Vistas:** Los datos para los desplegables (provincias, códigos postales, etc.) se cargan dinámicamente desde sus respectivos repositorios.
