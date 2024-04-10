# Pizzacase Project

Het Pizzacase project is een geavanceerd systeem voor het bestellen van pizza's, ontworpen om een veilige en efficiënte ervaring voor gebruikers te bieden. Dit document beschrijft de kernontwerppatronen en beveiligingsmechanismen die in dit project worden gebruikt.

## Inhoudsopgave

- [Ontwerppatronen](#ontwerppatronen)
  - [Singleton](#singleton)
  - [Composite](#composite)
  - [Visitor](#visitor)
- [Veiligheidsmechanismen](#veiligheidsmechanismen)
  - [SSL Encryptie](#ssl-encryptie)
  - [Message Authentication Code (MAC)](#message-authentication-code-mac)

## Ontwerppatronen

### Singleton

#### Toepassing in het project

De `Server` klasse binnen onze applicatie maakt gebruik van het Singleton-patroon om de databaseverbindingen en -operaties te beheren. Dit ontwerppatroon zorgt ervoor dat er slechts één instantie van de `Server` klasse kan bestaan tijdens de levensduur van de applicatie, waardoor een gecontroleerde toegang tot de database wordt gegarandeerd.

#### Implementatie

- **Privé Constructor:** Voorkomt directe instantiatie van buitenaf.
- **Privé Statische Instantie:** Een privé statische instantie van de `Server` klasse wordt intern beheerd.
- **Publieke Statische Methode:** De `getInstance()` methode biedt een wereldwijd toegangspunt tot de enige instantie.

```java
public class Server {
    private static Server instance;
    private Connection connection;

    private Server() {
        // Initialisatiecode...
    }

    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }
}
```
## Voordelen

- **Enkele Instantie:** Verzekert dat slechts één instantie van de `Server` klasse wordt aangemaakt.
- **Makkelijke Toegang:** Biedt een gemakkelijke toegang tot de `Server` instantie vanuit elke plek in de applicatie.
- **Lazy Initialization en Thread Safety:** De instantie wordt alleen aangemaakt wanneer nodig, met synchronisatie voor veilig gebruik in multithreaded omgevingen.
