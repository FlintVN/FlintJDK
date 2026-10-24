# HTTPS on FlintOS

HTTPS uses the firmware's authenticated ESP-TLS transport through the internal `flint.net.TlsSocket` binding. There is no fallback to the former unauthenticated Java TLS implementation. Firmware without the binding fails with IOException.

The ES3C28P configuration enables the full ESP-IDF CA bundle with cross-signed chain building. The platform adapter corrects missing validity dates only on synthetic bundle trust anchors (no peer DER); certificates received from the server retain all validity flags. `esp_crt_bundle_attach` supplies trust anchors; ESP-TLS checks certificate signatures, hostname and validity and authenticates the TLS handshake. Hardware-backed platform entropy supplies TLS randomness. Network time is synchronized before opening TLS; DNS and outbound NTP must work. Keep the firmware CA bundle current. Server certificates/private keys belong on the server and are not supplied by the board's CA bundle.

The native connection timeout is 250..20000 ms (zero chooses 20000 ms); initial network time synchronization can add up to 20 seconds. Read deadlines are checked between bounded native polls. HTTP CONNECT proxies are rejected. The platform does not currently expose the negotiated cipher name, so getCipherSuite() returns null. No Java TrustManager or insecure-verification switch is provided.
