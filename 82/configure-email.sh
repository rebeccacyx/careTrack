#!/bin/bash

# Email Configuration Script for CareTrack App
# This script helps configure SendGrid API Key for email sending

echo "=========================================="
echo "  CareTrack Email Configuration Setup"
echo "=========================================="
echo ""

# Check if SENDGRID_API_KEY is already set
if [ -n "$SENDGRID_API_KEY" ]; then
    echo "✅ SENDGRID_API_KEY is already set"
    echo "Current value: ${SENDGRID_API_KEY:0:10}..."
    echo ""
    read -p "Do you want to change it? (y/n): " change_it
    if [ "$change_it" != "y" ] && [ "$change_it" != "Y" ]; then
        echo "Keeping existing configuration."
        exit 0
    fi
fi

echo "To enable email sending (forget password & budget alerts),"
echo "you need to configure your SendGrid API Key."
echo ""
echo "📝 Steps to get SendGrid API Key:"
echo "1. Visit https://sendgrid.com/"
echo "2. Sign up or log in"
echo "3. Go to Settings → API Keys"
echo "4. Click 'Create API Key'"
echo "5. Select 'Full Access' or 'Restricted Access' with Mail Send permission"
echo "6. Copy the API Key (starts with SG.)"
echo ""
read -p "Enter your SendGrid API Key (or press Enter to skip): " api_key

if [ -z "$api_key" ]; then
    echo ""
    echo "⚠️  No API Key provided. Email sending will not work."
    echo "You can set it later by running:"
    echo "  export SENDGRID_API_KEY=SG.your_api_key_here"
    exit 0
fi

# Validate API Key format
if [[ ! $api_key == SG.* ]]; then
    echo ""
    echo "⚠️  Warning: API Key should start with 'SG.'"
    read -p "Do you want to continue anyway? (y/n): " continue
    if [ "$continue" != "y" ] && [ "$continue" != "Y" ]; then
        echo "Configuration cancelled."
        exit 1
    fi
fi

# Determine shell profile
if [ -f "$HOME/.zshrc" ]; then
    PROFILE="$HOME/.zshrc"
elif [ -f "$HOME/.bash_profile" ]; then
    PROFILE="$HOME/.bash_profile"
elif [ -f "$HOME/.bashrc" ]; then
    PROFILE="$HOME/.bashrc"
else
    PROFILE="$HOME/.profile"
fi

echo ""
echo "📝 Adding SENDGRID_API_KEY to $PROFILE..."

# Remove existing SENDGRID_API_KEY if present
sed -i.bak '/SENDGRID_API_KEY/d' "$PROFILE" 2>/dev/null || true

# Add new API Key
echo "" >> "$PROFILE"
echo "# SendGrid API Key for CareTrack Email Service" >> "$PROFILE"
echo "export SENDGRID_API_KEY=\"$api_key\"" >> "$PROFILE"

echo "✅ SENDGRID_API_KEY has been added to $PROFILE"
echo ""
echo "📋 Next steps:"
echo "1. Reload your shell configuration:"
echo "   source $PROFILE"
echo ""
echo "2. Verify the configuration:"
echo "   echo \$SENDGRID_API_KEY"
echo ""
echo "3. Restart the backend server for the changes to take effect"
echo ""
echo "4. Test email configuration:"
echo "   curl http://localhost:8081/api/mail/status"
echo ""
echo "✨ Email configuration complete!"



