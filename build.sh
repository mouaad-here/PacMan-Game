#!/bin/bash
# Build script for PacMan game
# Compiles Java files and copies resources to target directory

SOURCE_DIR="src/main/java/com/mouaad/pacman"
RESOURCES_DIR="src/main/resources/com/mouaad/pacman"
TARGET_DIR="target/classes/com/mouaad/pacman"

# Create target directory
mkdir -p "$TARGET_DIR"

# Compile Java files
echo "Compiling Java files..."
javac -d target/classes "$SOURCE_DIR"/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Copy resources
echo "Copying image resources..."
cp "$RESOURCES_DIR"/*.png "$TARGET_DIR/" 2>/dev/null

echo "Build complete! Output in: $TARGET_DIR"
echo ""
echo "To run the game use:"
echo "  java -cp target/classes com.mouaad.pacman.Main"
