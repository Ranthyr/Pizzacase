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
```
## Voordelen

- **Uniformiteit en Flexibiliteit:** Door alle GUI-componenten door dezelfde interface te laten implementeren, kunnen we gemakkelijk nieuwe schermen of panelen toevoegen zonder de algehele systeemarchitectuur te verstoren.
- **Eenvoudig Beheer:** Het beheren, toevoegen of verwijderen van GUI-componenten wordt gestroomlijnd, wat bijdraagt aan een schone en onderhoudbare codebasis.
- **Modulariteit:** Elk paneel kan onafhankelijk worden ontwikkeld en getest, wat de modulariteit van de applicatie verbetert.

### Visitor

#### Toepassing in het project

Het Visitor-patroon wordt in het Pizzacase-project gebruikt om diverse bewerkingen op pizza-bestellingen uit te voeren zonder de klassen van de objecten te wijzigen. Dit stelt ons in staat om nieuwe operaties toe te voegen en uit te voeren op bestellingen, zoals het verzamelen van statistieken, zonder de structuur of implementatie van de `PizzaOrder` klasse te moeten wijzigen.

#### Implementatie

De implementatie omvat een `OrderVisitor` interface met een `visit` methode voor elk type bestelling. De `PizzaOrder` klasse implementeert de `Order` interface, die een `accept` methode bevat. Deze methode accepteert een `OrderVisitor` die vervolgens de bijbehorende `visit` methode aanroept op het `PizzaOrder` object. De `OrderStatisticsVisitor` klasse implementeert de `OrderVisitor` interface en verzamelt verschillende statistieken over de bestellingen, zoals het totaal aantal bestellingen, gemiddeld aantal toppings per bestelling, en de meest voorkomende toppings.

```java
public interface OrderVisitor {
    void visit(PizzaOrder order);
}

public class PizzaOrder implements Order {
    // Velden en constructor
    public void accept(OrderVisitor visitor) {
        visitor.visit(this);
    }
    // Getter-methoden
}

public class OrderStatisticsVisitor implements OrderVisitor {
    // Velden voor statistieken
    @Override
    public void visit(PizzaOrder order) {
        // Logica voor het bijwerken van statistieken
    }
    // Methoden voor het ophalen van statistieken
}
```

#### Voordelen

- **Scheiding van Zorgen:** Door het Visitor-patroon kunnen operationele details gescheiden worden gehouden van de objectstructuur, wat de code schoon en onderhoudbaar houdt.
- **Uitbreidbaarheid:** Het patroon maakt het gemakkelijk om nieuwe operaties op de objectstructuur toe te voegen zonder deze te wijzigen, wat de uitbreidbaarheid van de applicatie verbetert.
- **Flexibiliteit:** Verschillende bezoekers kunnen worden gedefinieerd om verschillende operaties uit te voeren zonder dat de objecten die bezocht worden gewijzigd hoeven te worden.

### SSL Encryptie

#### Toepassing in het project

SSL (Secure Socket Layer) Encryptie is een essentieel beveiligingsmechanisme in het Pizzacase-project, gebruikt om een veilige communicatiekanaal tussen de client en de server te garanderen. Dit beveiligingspatroon zorgt voor vertrouwelijkheid, integriteit, en authenticatie in de gegevensoverdracht over het netwerk, wat van cruciaal belang is in applicaties waar gevoelige informatie, zoals klantgegevens en bestelgegevens, wordt uitgewisseld.

#### Implementatie

In zowel de `TCPSSLClient` als `TCPSSLServer` klassen wordt SSL/TLS gebruikt om een versleutelde verbinding op te zetten. Dit wordt bereikt door SSLContext te configureren met de juiste sleutel- en truststores, die respectievelijk de privésleutels en certificaten bevatten die nodig zijn voor de encryptie en het wederzijds vertrouwen tussen de client en server.

```java
KeyStore serverKeyStore = KeyStore.getInstance("JKS");
char[] password = "PizzaCase".toCharArray();
try (FileInputStream fis = new FileInputStream("src/network/tcp/keystore.jks")) {
    serverKeyStore.load(fis, password);
}

KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
keyManagerFactory.init(serverKeyStore, password);

SSLContext sslContext = SSLContext.getInstance("TLS");
sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
```

Het `keystore.jks` bestand bevat de privésleutel en het publieke certificaat van de server. De `truststore.jks` bevat de certificaten die de client vertrouwt, waaronder het publieke certificaat van de server (`server.crt`). Dit zorgt ervoor dat zowel de client als de server de identiteit van de ander kunnen verifiëren.

#### Voordelen

- **Vertrouwelijkheid:** Door alle communicatie te versleutelen, zorgt SSL ervoor dat gevoelige informatie beschermd is tegen onderschepping door derden.
- **Integriteit:** SSL biedt mechanismen om te controleren of de gegevens niet zijn gewijzigd tijdens de overdracht, waardoor de integriteit van de verzonden informatie wordt gewaarborgd.
- **Authenticatie:** Door gebruik te maken van certificaten kan SSL de identiteit van zowel de client als de server verifiëren, wat bijdraagt aan een veiligere communicatie.
