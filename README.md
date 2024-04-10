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
#### Voordelen

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
#### Voordelen

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

## Veiligheidsmechanismen

### Data Encryption (Gegevensversleuteling)

#### Toepassing in het project

Gegevensversleuteling speelt een cruciale rol in het Pizzacase-project om de privacy en veiligheid van de gebruikersgegevens te waarborgen. Dit omvat SSL Encryptie voor het beveiligen van data in transit en het gebruik van Message Authentication Code (MAC) voor het verifiëren van de integriteit en authenticiteit van berichten.

#### Implementatie

##### SSL Encryptie

SSL (Secure Socket Layer) Encryptie is een essentieel beveiligingsmechanisme in het Pizzacase-project, gebruikt om een veilige communicatiekanaal tussen de client en de server te garanderen. Dit beveiligingspatroon zorgt voor vertrouwelijkheid, integriteit, en authenticatie in de gegevensoverdracht over het netwerk, wat van cruciaal belang is in applicaties waar gevoelige informatie, zoals klantgegevens en bestelgegevens, wordt uitgewisseld.

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

##### Message Authentication Code (MAC)

In het Pizzacase-project wordt de Message Authentication Code (MAC) gebruikt als een middel om de integriteit en authenticiteit van de berichten te verifiëren die tussen de `UDPClient` en `UDPServer` worden uitgewisseld. Dit zorgt ervoor dat de gegevens niet zijn gewijzigd tijdens de overdracht en bevestigt dat het bericht afkomstig is van de legitieme bron.

De implementatie maakt gebruik van de HmacSHA256-algoritme voor het genereren en verifiëren van de MAC. Wanneer de `UDPClient` een bericht verzendt, genereert het een HMAC op basis van het bericht en een gedeelde geheime sleutel. Dit MAC wordt dan toegevoegd aan het bericht voordat het wordt verzonden. Bij ontvangst van het bericht scheidt de `UDPServer` de MAC van het bericht en genereert een nieuwe MAC op basis van het ontvangen bericht en de gedeelde geheime sleutel. Als de nieuw gegenereerde MAC overeenkomt met de ontvangen MAC, is het bericht geverifieerd.

```java
private byte[] generateHMAC(byte[] message) {
    try {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        return mac.doFinal(message);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
        System.err.println("Fout bij het genereren van HMAC: " + e.getMessage());
        return null;
    }
}

private boolean verifyHMAC(byte[] message, byte[] receivedMac) {
    try {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] calculatedMac = mac.doFinal(message);
        return Arrays.equals(calculatedMac, receivedMac);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
        System.err.println("Fout bij het verifiëren van HMAC: " + e.getMessage());
        return false;
    }
}
```

#### Voordelen

- **Vertrouwelijkheid:** SSL en MAC helpen om de vertrouwelijkheid van de gegevens te bewaren door te verzekeren dat alleen geautoriseerde partijen toegang hebben tot de informatie.
- **Integriteit:** Ze garanderen dat de data niet is gewijzigd tijdens de overdracht, waardoor de betrouwbaarheid van de communicatie tussen client en server wordt gewaarborgd.
- **Authenticatie:** SSL biedt een mechanisme voor het verifiëren van de identiteit van de partijen, terwijl MAC de authenticiteit van de verzonden berichten verifieert.
