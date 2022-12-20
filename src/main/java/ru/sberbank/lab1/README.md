# Шаг 1

Убран лишний код, подключен актуатор, замерено время работы исходного сервиса.

Запрос ``GET http://localhost:8091/lab1/weather?days=50``

Время работы ``GET http://localhost:8091/actuator/metrics/LAB_1_weather``

```
{
  "name": "LAB_1_weather",
  "description": null,
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1.0
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 11.495675304
    },
    {
      "statistic": "MAX",
      "value": 11.495675304
    }
  ],
  "availableTags": [
    {
      "tag": "exception",
      "values": [
        "None"
      ]
    },
    {
      "tag": "method",
      "values": [
        "GET"
      ]
    },
    {
      "tag": "uri",
      "values": [
        "/lab1/weather"
      ]
    },
    {
      "tag": "outcome",
      "values": [
        "SUCCESS"
      ]
    },
    {
      "tag": "status",
      "values": [
        "200"
      ]
    }
  ]
}
```

Исходное время запроса получилось 11.495675304 секунд при запросе погоды за 50 дней.