# PC_CRAWLER

A powerful web crawler and content extraction tool built with Apache Tika integration.

## Description

PC_CRAWLER is a web crawling application that navigates through websites, extracts content, and processes the data using Apache Tika. This tool helps automate the extraction of text, metadata, and structured information from various file formats and web pages.

## Features

- Web crawling with configurable depth and scope
- Content extraction from multiple file types (PDF, DOC, HTML, etc.)
- Metadata analysis using Apache Tika
- Customizable crawling rules and filters
- Data export in various formats

## Installation

# Clone the repository

```bash
git clone https://github.com/CarlosCamberoR/PC_CRAWLER_TIKA.git
```

## Usage

```python
from pc_crawler import Crawler

# Initialize the crawler
crawler = Crawler(start_url="https://example.com", depth=2)

# Start crawling
results = crawler.start()

# Process the results
crawler.export_results(results, format="json")
```

## Configuration

Edit the `config.json` file to customize the crawler's behavior:

```json
{
    "max_depth": 3,
    "follow_external_links": false,
    "user_agent": "PC_Crawler/1.0",
    "delay_between_requests": 1,
    "file_types": ["pdf", "doc", "html", "txt"]
}
```

## Dependencies

- Python 3.7+
- Apache Tika
- Requests
- BeautifulSoup4

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.