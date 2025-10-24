#!/bin/bash
# ============================================================
# 🔁 Update Script for Webshop Deployment
# Holt neue Änderungen von GitHub, baut Images neu & startet.
# ============================================================

cd /opt/shop/Webshop

echo "📥 Pulling latest changes..."
git pull origin main || git pull origin master

echo "🛠️ Rebuilding Docker containers..."
docker compose down
docker compose up -d --build

echo "✅ Update complete!"
docker compose ps