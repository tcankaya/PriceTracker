Price Tracker

A Java-based automated price tracker that monitors product prices from e-commerce websites, handles cookie popups, and alerts when prices drop. Data is stored in JSON for persistent tracking.

Features
	•	Automated Price Fetching: Reads product URLs from a JSON file and fetches the latest prices.
	•	Cookie Handling: Detects and accepts cookie popups using IDs, classes, or button text.
	•	Price Alerts: Notifies on price drops (console output, easily extendable to email or Telegram).
	•	Brand-Specific Selectors: Supports custom CSS selectors per brand for accurate price scraping.
	•	Persistent Storage: Saves product info and last known prices in JSON.

How It Works
	1.	Add Products: Add a product URL and brand/product name in prices.json.
	2.	Automated Checks: The system checks prices every 30 minutes (hardcoded but extendable).
	3.	Cookie Handling: Automatically accepts cookie popups where possible.
	4.	Price Detection: Uses brand-specific or general CSS selectors to locate price elements.
	5.	Notifications: Alerts you when the price drops.
	6.	Persistent Updates: Updates prices.json with the latest prices.

Known Issues
	•	Cookie Handling Limitations: Some websites may not have their popups detected because they use different types of selectors not covered yet.
	•	Hardcoded Interval: Checks run every 30 minutes; dynamic scheduling is not implemented.
	•	Price Detection: Complex layouts may prevent accurate scraping if brand-specific selectors are missing.

Future Improvements
	•	Automate adding new products instead of editing JSON manually.
	•	Expand cookie selector coverage for better reliability.
	•	Allow customizable checking intervals.
	•	Integrate email, Telegram, or push notifications.
	•	Improve price parsing for complex or site-specific layouts.

Getting Started

* Clone the repository. *
 
- git clone https://github.com/tcankaya/price-tracker.git

* All dependencies handled via pom.xml (Maven) 
  
* Add your products in prices.json with URL *
  
* Run Main.java to start the tracker *

* Example json file can be found in the project *

Tech Stack
	•	Java 24
	•	Selenium WebDriver + ChromeDriver
	•	Jackson for JSON handling
	•	TimerTask for scheduling

License
This project is open source. Feel free to use and improve it.
