#!/bin/bash
# ============================================================
# 🧾 PostgreSQL Backup Script for webshop_db
# Speichert tägliche Dumps in /opt/shop/backups/YYYY-MM-DD.sql
# ============================================================

BACKUP_DIR="/opt/shop/backups"
DATE=$(date +%F)
CONTAINER="webshop_db"
DB_NAME="webshop"
DB_USER="shop"

mkdir -p "$BACKUP_DIR"

docker exec -t $CONTAINER pg_dump -U $DB_USER $DB_NAME > "$BACKUP_DIR/${DB_NAME}_${DATE}.sql"

echo "✅ Backup erstellt: $BACKUP_DIR/${DB_NAME}_${DATE}.sql"