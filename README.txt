Microservicio de Rutas

El MS rutas-service arma una hoja de ruta por camión y día, recibe las paradas que manda la asignación 
y deja que el conductor registre si el retiro se hizo o falló. 
Cuando registra el resultado, avisa a solicitudes-service para que el vecino vea el estado final.


Desarrollado con Java 25 y Springboot 4.1.1 y documentacion mediante Swagger