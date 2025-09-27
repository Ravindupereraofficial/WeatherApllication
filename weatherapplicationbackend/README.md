# Weather Application Backend

A secure REST API that retrieves weather information from OpenWeatherMap API with Auth0 authentication and caching.

## Features

- **Weather API Integration**: Fetches weather data from OpenWeatherMap API
- **Auth0 Authentication**: Secure JWT-based authentication with Auth0
- **Caching**: Weather responses cached for 5 minutes using Caffeine
- **Multi-Factor Authentication**: Email verification through Auth0
- **Clean Architecture**: Organized with controller, service, config, and model packages
- **Unit Tests**: Comprehensive tests for service and controller layers

## Prerequisites

- Java 17+
- Maven 3.6+
- Auth0 account
- OpenWeatherMap API key

## Setup Instructions

### 1. Auth0 Configuration

1. Create an Auth0 account at [auth0.com](https://auth0.com)
2. Create a new Auth0 application:
   - Go to Applications → Create Application
   - Choose "Single Page Web Applications"
   - Note down the Domain and Client ID

3. Create an API in Auth0:
   - Go to APIs → Create API
   - Set Name: "Weather API"
   - Set Identifier: "https://weather-api.example.com" (use this as audience)

4. Create a test user:
   - Go to User Management → Users → Create User
   - Email: `careers@fidenz.com`
   - Password: `Pass#fidenz`

5. Enable Multi-Factor Authentication:
   - Go to Security → Multi-factor Auth
   - Enable Email
   - Set up verification rules

### 2. Application Configuration

Update `src/main/resources/application.properties`:

```properties
# Auth0 Configuration
auth0.audience=https://weather-api.example.com
auth0.domain=your-auth0-domain.auth0.com
```

Replace:
- `your-auth0-domain.auth0.com` with your Auth0 domain
- `https://weather-api.example.com` with your Auth0 API identifier

### 3. Running the Application

```bash
# Clone the repository
cd weatherapplicationbackend

# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication Required

All endpoints require a valid JWT token from Auth0.

Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Endpoints

#### Get Weather by City Code
```http
GET /api/weather/{cityCode}
```

**Example:**
```http
GET /api/weather/1248991
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "CityCode": "1248991",
  "CityName": "Colombo",
  "Temp": "33.0",
  "Status": "Clear"
}
```

#### Get All Weather Data
```http
GET /api/weather/all
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "List": [
    {
      "CityCode": "1248991",
      "CityName": "Colombo",
      "Temp": "33.0",
      "Status": "Clouds"
    },
    {
      "CityCode": "1850147",
      "CityName": "Tokyo",
      "Temp": "8.6",
      "Status": "Clear"
    }
  ]
}
```

## Available Cities

The application supports weather data for the following cities:

| City Code | City Name |
|-----------|-----------|
| 1248991   | Colombo   |
| 1850147   | Tokyo     |
| 2644210   | Liverpool |
| 2988507   | Paris     |
| 2147714   | Sydney    |
| 4930956   | Boston    |
| 1796236   | Shanghai  |
| 3143244   | Oslo      |

## Caching

- Weather responses are cached for **5 minutes**
- Cache key: City code
- Cache implementation: Caffeine
- Maximum cache size: 100 entries

## Security Features

- **JWT Validation**: All requests validated against Auth0
- **Audience Validation**: Custom audience validator
- **CORS Support**: Configurable cross-origin requests
- **Error Handling**: Comprehensive error responses

## Testing

Run unit tests:
```bash
mvn test
```

The test suite includes:
- Service layer tests with mocking
- Controller layer tests
- Security configuration tests

## Architecture

```
src/main/java/edu/icet/ecom/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── exception/        # Exception handlers
├── model/           # Data models
├── service/         # Business logic
└── Main.java        # Application entry point
```

## Environment Variables

The following environment variables can be used instead of application.properties:

- `AUTH0_AUDIENCE`: Auth0 API audience
- `AUTH0_DOMAIN`: Auth0 domain
- `OPENWEATHERMAP_API_KEY`: OpenWeatherMap API key

## Error Handling

The application provides structured error responses:

```json
{
  "timestamp": "2025-01-01T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/weather"
}
```

## Monitoring

Logs are configured with different levels:
- `DEBUG`: Application flow and security details
- `INFO`: Request/response information
- `WARN`: Authentication warnings
- `ERROR`: Errors and exceptions

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.