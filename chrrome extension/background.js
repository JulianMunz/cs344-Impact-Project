
chrome.runtime.onMessage.addListener(function(message, sender) {
    if (!message.run) return;

    fetch('http://localhost:8080').then(r => r.text()).then(result => {
    // Result now contains the response text, do what you want...
    })
});