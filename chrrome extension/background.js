
chrome.runtime.onMessage.addListener(function(message, sender, sendResponse) {
    if (!message.run) return;

    const url = message.url;
    console.log('Scraping url: ' + url);
    //wait for response
    //data is the list of jsons
    const newCall = 'http://localhost:8080/api/getPredictions?url=' + url;
    console.log("get results");
    fetch(newCall).then(function(response) {
      if (response.status !== 200) {
        console.log({name:'Error', num_par: '0'});
        return;
      }
      response.json().then(function(returned_data) {
        console.log(returned_data);
        sendResponse(returned_data)
      });
    });
    return true;
});
