# Hivehook (Java)

Official Java client for [Hivehook](https://hivehook.com), webhook infrastructure for modern teams (inbound and outbound).

Latest release: **0.1.1** on [Maven Central](https://central.sonatype.com/artifact/com.hivehook/sdk).

## Install

### Gradle

```kotlin
dependencies {
    implementation("com.hivehook:sdk:0.1.1")
}
```

### Maven

```xml
<dependency>
  <groupId>com.hivehook</groupId>
  <artifactId>sdk</artifactId>
  <version>0.1.1</version>
</dependency>
```

## Quick start

```java
import com.hivehook.sdk.HivehookClient;
import com.hivehook.sdk.types.Source;
import java.util.Map;

try (HivehookClient client = HivehookClient.builder()
        .baseUrl("http://localhost:8080")
        .apiKey(System.getenv("HIVEHOOK_API_KEY"))
        .build()) {

    Source source = client.sources().create(
        "Stripe production",
        "stripe-prod",
        "stripe",
        Map.of("secret", "whsec_...")
    );

    System.out.printf("created source %s. POST webhooks to /ingest/%s%n",
        source.id(), source.slug());
}
```

## Webhook signature verification

```java
import com.hivehook.sdk.webhook.Webhook;

String signature = request.getHeader(Webhook.HEADER_SIGNATURE);
long timestamp = Long.parseLong(request.getHeader(Webhook.HEADER_TIMESTAMP));
boolean ok = Webhook.verify(body, "your-signing-secret", signature, timestamp, 300);
```

## Requirements

- Java 17+

## Documentation

See the full reference at [hivehook.com/docs](https://hivehook.com/docs).

## License

MIT. See [LICENSE](LICENSE).
