  GNU nano 7.2                                            status.sh
#!/bin/bash
# ============================================================
# 📊 Status Script for Webshop Stack
# Zeigt Container-Zustand & Logs in Kurzform an
# ============================================================

cd /opt/shop/Webshop

echo "🩺 Container Status:"
docker compose ps

echo
echo "📋 Health States:"
docker ps -q | xargs -r docker inspect --format='{{.Name}}: {{if .State.Health}}{{.State.Health.Status}}{{else}}(no hea>

echo
echo "📜 Letzte App-Logs (20 Zeilen):"
docker compose logs --tail=20 app