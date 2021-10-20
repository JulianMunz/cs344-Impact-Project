
chrome.runtime.onMessage.addListener(function(message, sender) {
    if (!message.run) return;

    const url = message.url;
    console.log('Scraping url: ' + url);
    const apiCall = 'http://localhost:8080/api/scrape?url=' + url;
    console.log('API call made: ' + apiCall);
    fetch(apiCall).then( function(res) {
      //wait for response
      if (res.status !== 200) {
        console.log({name:'Error', num_par: '0'});
        return;
      }
      res.json().then(function(data) {
        console.log(data);
        //data is the list of jsons
      });
    });
    return true;
});
