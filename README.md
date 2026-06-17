BD Cuentas Corrientes Personal

Proyecto desarrollado en Java con Maven, JavaFX y MySQL para el mantenimiento de tablas referenciales de una base de datos de cuentas corrientes personales.

Descripción del proyecto

El sistema permite conectarse a una base de datos MySQL y realizar operaciones básicas sobre tablas referenciales. La aplicación cuenta con una interfaz gráfica desarrollada en JavaFX, donde el usuario puede seleccionar una tabla y ejecutar operaciones de mantenimiento.

Entre las operaciones principales se encuentran:

Adicionar registros.
Modificar registros.
Eliminar registros de forma lógica.
Inactivar registros.
Reactivar registros.
Actualizar y visualizar datos en una grilla.
Tecnologías utilizadas
Java
Maven
JavaFX
MySQL
MySQL Connector/J
dotenv-java
NetBeans
Estructura del proyecto
BD_Cuentas_Corrientes_Personal/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── mycompany/
│                   └── coneccion_bd/
│                       ├── Coneccion_BD.java
│                       ├── ConexionBD.java
│                       └── Launcher.java
│
├── pom.xml
├── nbactions.xml
├── .gitignore
└── README.md
