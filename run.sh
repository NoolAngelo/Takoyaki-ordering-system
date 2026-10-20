#!/bin/bash

# Gelo's Takoyaki Ordering System - Quick Start Script

echo "🍡 Gelo's Takoyaki Ordering System - Enhanced Version"
echo "=================================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java to run this application."
    exit 1
fi

# Check if the Java file exists
if [ ! -f "TakoyakiOrderingSystem.java" ]; then
    echo "❌ TakoyakiOrderingSystem.java not found in current directory."
    exit 1
fi

# Compile the Java file
echo "🔨 Compiling TakoyakiOrderingSystem.java..."
if javac TakoyakiOrderingSystem.java; then
    echo "✅ Compilation successful!"
    echo ""
    echo "🚀 Starting Gelo's Takoyaki Ordering System..."
    echo "Features: Order Management | Discounts | Combos | Inventory | Customer Info"
    echo ""
    echo "=================================================="

    # Run the application
    java TakoyakiOrderingSystem
else
    echo "❌ Compilation failed. Please check for errors."
    exit 1
fi

echo ""
echo "Thank you for using Gelo's Takoyaki Ordering System! 🍡"
