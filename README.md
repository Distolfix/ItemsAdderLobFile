# ItemsAdderLobFile Plugin

Un plugin per Minecraft che integra automaticamente LobFile con ItemsAdder, aggiornando l'URL del resource pack con l'ultimo pack caricato su LobFile.

## Funzionalità

- **Controllo automatico**: Verifica ogni 30 minuti (configurabile) se c'è un nuovo resource pack su LobFile
- **Aggiornamento automatico**: Aggiorna automaticamente la configurazione di ItemsAdder con il nuovo URL
- **Comandi di gestione**: Permette controlli manuali e gestione della configurazione
- **Integrazione trasparente**: Si integra perfettamente con ItemsAdder senza interferenze

## Installazione

1. Compila il plugin con `gradle build` (richiede Java 21)
2. Copia il file JAR generato nella cartella `plugins/` del tuo server
3. Configura la tua API key di LobFile nel file `secret.yml`
4. Riavvia il server

## Configurazione

### secret.yml
```yaml
lobfile:
  api-key: "LA_TUA_API_KEY_LOBFILE"
  base-url: "https://lobfile.com/api/v1"
  check-interval-minutes: 30
```

### Dove trovare la tua API key
Vai su https://lobfile.com/api-docs#getting-started per ottenere la tua API key.

## Comandi

- `/lobfile check` - Controlla manualmente per aggiornamenti
- `/lobfile status` - Mostra lo stato attuale del plugin
- `/lobfile test` - Testa la connessione all'API di LobFile
- `/lobfile reload` - Ricarica la configurazione del plugin

## Permessi

- `lobfile.admin` - Accesso ai comandi del plugin (default: OP)

## Come funziona

1. Il plugin si connette all'API di LobFile usando la tua API key
2. Ogni 30 minuti (o l'intervallo configurato), controlla l'ultimo resource pack caricato
3. Se trova un pack più recente, aggiorna automaticamente la configurazione di ItemsAdder:
   - Imposta `resource-pack.hosting.external-host.enabled: true`
   - Aggiorna `resource-pack.hosting.external-host.url` con il nuovo URL
   - Disabilita altri metodi di hosting (lobfile, self-host, no-host)
4. Esegue automaticamente `/iazip` per rigenerare il resource pack

## Requisiti

- Java 21+
- Paper/Spigot 1.21+
- ItemsAdder (opzionale ma raccomandato)
- Account LobFile con API key valida

## Supporto

Se hai problemi con il plugin, controlla i log del server per eventuali errori. Assicurati che:
- La tua API key sia corretta
- ItemsAdder sia installato e configurato
- Il server abbia accesso a internet per raggiungere l'API di LobFile