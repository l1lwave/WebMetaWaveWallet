# Application Configuration Setup

Before running the project, you need to populate configuration data in the `application.properties` file.

## Required Keys and Settings

### 1. Database (Google Cloud SQL)
- `spring.datasource.url` - Your Cloud SQL database URL in the format:  
  `jdbc:mysql:///<db-name>?cloudSqlInstance=<instance-name>&socketFactory=com.google.cloud.sql.mysql.SocketFactory`
- `spring.datasource.username` - Database username
- `spring.datasource.password` - Database password

[Google Cloud SQL Documentation](https://cloud.google.com/sql/docs/mysql/connect-app-engine-standard)

### 2. CoinMarketCap API
- `coinmarketcap.api.key` - Your CoinMarketCap API key
- `coinmarketcap.api.url` - Base API URL (typically `https://pro-api.coinmarketcap.com`)

[Get API Key](https://pro.coinmarketcap.com/signup)

### 3. Email Settings (Gmail)
- `spring.mail.password` - Gmail application password

How to create an app password:
1. Enable 2FA in your Google account
2. Go to [Security Settings](https://myaccount.google.com/security)
3. Under "App passwords", generate a new password

### 4. Application Port
- `server.port` - Defaults to 8080, will use PORT environment variable on Heroku

## Important!
Never commit files with real API keys to Git. Either:
- Add `application.properties` to `.gitignore`, or
- Use environment variables in production

Consider using environment variables or secret management for sensitive data.
