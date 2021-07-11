# Minecraft2QQ
a fork of project-polar/MiraiAdapter ,which focused on Bukkit-like Servers.  
It connects mirai-api-http (version 1.X) then make a bridge between your Minecraft Server and specified QQ Group.  

# Configuration
Example:  
```json
{
  "baseUrl": "http://localhost:8080/",
  "QQ": 3325273573,  // bot qq
  "authKey": ".....",
  "displayMessage": true, // useless in this edition
  "reconnectLimit": 10,
  "reconnectTimeWait": 3000,
  "debug": true,
  "targetGroup": 114514 // qq group
}
```