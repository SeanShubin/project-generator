#!/usr/bin/env bash

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_GENERATOR_DIR="$(dirname "$SCRIPT_DIR")"
SECRETS_DIR="$PROJECT_GENERATOR_DIR/secrets"
PROJECTS_FILE="$SECRETS_DIR/projects.txt"
CONSOLE_JAR="$PROJECT_GENERATOR_DIR/console/target/project-generator-console.jar"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=================================="
echo "Project Generator - Regenerate All"
echo "=================================="
echo

# Step 1: Build the project generator
echo "Step 1: Building project generator..."
cd "$PROJECT_GENERATOR_DIR"
if mvn clean package -q; then
    echo -e "${GREEN}✓${NC} Project generator built successfully"
else
    echo -e "${RED}✗${NC} Failed to build project generator"
    exit 1
fi
echo

# Check if projects file exists
if [ ! -f "$PROJECTS_FILE" ]; then
    echo -e "${RED}✗${NC} Projects file not found: $PROJECTS_FILE"
    exit 1
fi

# Count total projects
total_projects=$(grep -v '^#' "$PROJECTS_FILE" | grep -v '^[[:space:]]*$' | wc -l | tr -d ' ')
echo "Found $total_projects projects to process"
echo

# Track results
success_count=0
fail_count=0
failed_projects=()

# Step 2: Iterate through projects
while IFS= read -r project_path || [ -n "$project_path" ]; do
    # Skip empty lines and comments
    [[ -z "$project_path" || "$project_path" =~ ^[[:space:]]*# ]] && continue

    echo "=================================="
    echo "Processing: $project_path"
    echo "=================================="

    # Check if directory exists
    if [ ! -d "$project_path" ]; then
        echo -e "${RED}✗${NC} Directory not found: $project_path"
        ((fail_count++))
        failed_projects+=("$project_path (directory not found)")
        echo
        continue
    fi

    # Change to project directory
    cd "$project_path"
    project_name=$(basename "$project_path")

    # Run project generator
    echo "Running project generator..."
    if java -jar "$CONSOLE_JAR" 2>&1 | tail -5; then
        echo -e "${GREEN}✓${NC} Project generator completed"
    else
        echo -e "${RED}✗${NC} Project generator failed"
        ((fail_count++))
        failed_projects+=("$project_name (generator failed)")
        echo
        continue
    fi

    # Verify the project
    echo "Verifying project with mvn verify..."
    if mvn verify -q 2>&1 | tail -10; then
        echo -e "${GREEN}✓${NC} Project verified successfully"
        ((success_count++))
    else
        echo -e "${RED}✗${NC} Project verification failed"
        ((fail_count++))
        failed_projects+=("$project_name (verification failed)")
    fi

    echo
done < "$PROJECTS_FILE"

# Summary
echo "=================================="
echo "Summary"
echo "=================================="
echo "Total projects: $total_projects"
echo -e "${GREEN}Successful: $success_count${NC}"
echo -e "${RED}Failed: $fail_count${NC}"

if [ $fail_count -gt 0 ]; then
    echo
    echo "Failed projects:"
    for failed in "${failed_projects[@]}"; do
        echo -e "  ${RED}✗${NC} $failed"
    done
    exit 1
else
    echo
    echo -e "${GREEN}All projects processed successfully!${NC}"
    exit 0
fi
