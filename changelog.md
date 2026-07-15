# GregTech Modern Utilities version 2.10.0

* Fully replaced the Wireless Active Transformer with the WEB Hub and WEB Receiver
  * WEB Hubs transport power to WEB Receivers of the same frequency
  * WEB Hubs retain the same structure as the PTERB/WAT but no longer allow energy outputs
  * Only the WEB Hub requires coolant, if enabled
    * Coolant multiplier now multiplies the logarithm base 2 of voltage * amperage, not the full amount