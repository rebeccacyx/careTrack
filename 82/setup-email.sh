#!/bin/bash

echo "=========================================="
echo "  📧 CareTrack Email Configuration Helper"
echo "=========================================="
echo ""

# Check if already configured
if [ -n "$SENDGRID_API_KEY" ]; then
    echo "✅ SENDGRID_API_KEY is already configured"
    echo "Current value: ${SENDGRID_API_KEY:0:15}..."
    echo ""
    read -p "Do you want to reconfigure? (y/n): " reconfig
    if [ "$reconfig" != "y" ] && [ "$reconfig" != "Y" ]; then
        echo "Keeping existing configuration."
        exit 0
    fi
fi

echo "📋 Configuration Steps:"
echo ""
echo "1. If you don't have a SendGrid account yet, please register first:"
echo "   → Visit https://sendgrid.com/"
echo "   → Click 'Sign Up' or 'Start for Free'"
echo "   → Fill in your information and verify your email"
echo ""
echo "2. Create API Key:"
echo "   → Log in to SendGrid"
echo "   → Settings → API Keys → Create API Key"
echo "   → Enter a name (e.g., CareTrack)"
echo "   → Select 'Full Access' (easiest option)"
echo "   → Copy the API Key (format: SG.xxxxxxxxxxxx)"
echo "   ⚠️  Note: API Key is shown only once, copy it immediately!"
echo ""
read -p "Do you already have a SendGrid API Key? (y/n): " has_key

if [ "$has_key" != "y" ] && [ "$has_key" != "Y" ]; then
    echo ""
    echo "📝 Please complete the following steps first:"
    echo ""
    echo "[Step 1] Register SendGrid Account"
    echo "1. Open browser and visit: https://sendgrid.com/"
    echo "2. Click 'Sign Up' in the top right corner"
    echo "3. Fill in email, username, password"
    echo "4. Verify your email (check inbox)"
    echo ""
    echo "[Step 2] Create API Key"
    echo "1. Log in to SendGrid"
    echo "2. Left menu: Settings → API Keys"
    echo "3. Click 'Create API Key'"
    echo "4. Name: CareTrack App"
    echo "5. Permissions: Select 'Full Access' (easiest option)"
    echo "6. Click 'Create & View'"
    echo "7. ⚠️  Copy the API Key immediately (format: SG.xxxxxxxxxxxx)"
    echo ""
    echo "After completing these steps, run this script again, or enter the API Key to continue:"
    read -p "Enter your SendGrid API Key (or press Enter to exit): " api_key
    if [ -z "$api_key" ]; then
        echo ""
        echo "OK, you can run this script again after you have the API Key ready."
        echo "Or you can configure it manually:"
        echo "export SENDGRID_API_KEY=SG.your_api_key_here"
        exit 0
    fi
else
    echo ""
    read -p "Enter your SendGrid API Key (format: SG.xxxxxxxxxxxx): " api_key
fi

if [ -z "$api_key" ]; then
    echo "❌ No API Key entered, configuration cancelled"
    exit 1
fi

# Validate format
if [[ ! $api_key == SG.* ]]; then
    echo ""
    echo "⚠️  Warning: API Key usually starts with 'SG.'"
    read -p "Are you sure this API Key is correct? (y/n): " confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        echo "Configuration cancelled"
        exit 1
    fi
fi

# Determine configuration file
if [ -f "$HOME/.zshrc" ]; then
    PROFILE="$HOME/.zshrc"
    SHELL_NAME="zsh"
elif [ -f "$HOME/.bash_profile" ]; then
    PROFILE="$HOME/.bash_profile"
    SHELL_NAME="bash"
elif [ -f "$HOME/.bashrc" ]; then
    PROFILE="$HOME/.bashrc"
    SHELL_NAME="bash"
else
    PROFILE="$HOME/.profile"
    SHELL_NAME="profile"
fi

echo ""
echo "📝 Configuring $SHELL_NAME configuration file..."

# Remove old configuration if exists
if grep -q "SENDGRID_API_KEY" "$PROFILE" 2>/dev/null; then
    sed -i.bak '/SENDGRID_API_KEY/d' "$PROFILE" 2>/dev/null
    echo "   Removed old configuration"
fi

# Add new configuration
echo "" >> "$PROFILE"
echo "# SendGrid API Key for CareTrack Email Service" >> "$PROFILE"
echo "export SENDGRID_API_KEY=\"$api_key\"" >> "$PROFILE"

echo "✅ API Key has been added to $PROFILE"
echo ""

# Export to current session immediately
export SENDGRID_API_KEY="$api_key"
echo "✅ API Key has been set for current terminal session"
echo ""

echo "=========================================="
echo "  ✨ Configuration Complete!"
echo "=========================================="
echo ""
echo "📋 Next Steps:"
echo ""
echo "1. Reload configuration (or close and reopen terminal):"
echo "   source $PROFILE"
echo ""
echo "2. Restart backend service:"
echo "   cd backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "3. Test email configuration:"
echo "   Visit: http://localhost:8081/api/mail/status"
echo "   Should show: \"configured\": true"
echo ""
echo "4. Send test email:"
echo "   curl -X POST \"http://localhost:8081/api/mail/test?to=your-email@example.com\""
echo ""
echo "🎉 After configuration, email features will be available!"
echo ""
echo "Email features include:"
echo "  ✅ Forget password email (when user requests password reset)"
echo "  ✅ Budget alert email (when budget usage reaches 80% or 100%)"
echo "  ✅ Message notification email (when user receives a new message)"
echo ""
