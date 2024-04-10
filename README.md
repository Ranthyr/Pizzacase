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

### Composite

#### Toepassing in het project

Het Composite-patroon wordt gebruikt in de GUI van het Pizzacase project om een modulaire en flexibele interfacestructuur te creëren. Door dit patroon toe te passen, kan onze applicatie verschillende soorten gebruikersinterfacecomponenten (panels) behandelen via een gemeenschappelijke interface (`GUIComponent`). Dit stelt ons in staat om individuele componenten (bijvoorbeeld `ConnectionPanel`, `OrderPanel`, enz.) en hun composities uniform te behandelen, waardoor de complexiteit van het beheer en de uitbreiding van de GUI wordt verminderd.

#### Implementatie

De `ClientGUI` klasse maakt gebruik van een `CardLayout` om verschillende `JPanel` objecten te beheren, die allemaal het `GUIComponent` interface implementeren. Dit zorgt voor een gestructureerde maar flexibele manier om tussen verschillende schermen in de applicatie te navigeren. Elk paneel binnen de applicatie kan als een onafhankelijk component worden gezien dat zijn eigen verantwoordelijkheden heeft, terwijl het nog steeds past binnen de algehele GUI-structuur.

```java
public interface GUIComponent {
    Component getComponent();
}

class ConnectionPanel extends JPanel implements GUIComponent {
    // Implementatie...
}

class OrderPanel extends JPanel implements GUIComponent {
    // Implementatie...
}

class AddressPanel extends JPanel implements GUIComponent {
    // Implementatie...
}

class ConfirmPanel extends JPanel implements GUIComponent {
    // Implementatie...
}

## Voordelen

- **Uniformiteit en Flexibiliteit:** Door alle GUI-componenten door dezelfde interface te laten implementeren, kunnen we gemakkelijk nieuwe schermen of panelen toevoegen zonder de algehele systeemarchitectuur te verstoren.
- **Eenvoudig Beheer:** Het beheren, toevoegen of verwijderen van GUI-componenten wordt gestroomlijnd, wat bijdraagt aan een schone en onderhoudbare codebasis.
- **Modulariteit:** Elk paneel kan onafhankelijk worden ontwikkeld en getest, wat de modulariteit van de applicatie verbetert.
