
let colorDivs = document.getElementById("colorDivs");

colorDivs.addEventListener("click", async () => {
    let [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

    chrome.scripting.executeScript({
      target: { tabId: tab.id },
      function: showColoring,
    });
  });

  // The body of this function will be executed as a content script inside the
  // current page
  function showColoring() {
    var text = "true";
    chrome.runtime.sendMessage({run: true, data:{text}, url: window.location.href}, function(response) {
      console.log(response);
      var all = document.querySelectorAll("*");
      for (var i = 0, max=all.length; i < max; i++) {
        if (response[i] == "none") {

        } else {
          all[i].style.backgroundColor = "#a5e0e4";
        }
      }
      console.log(all.length);
    });
    // document.getElementById("content").style.backgroundColor = "blue";
    // document.getElementById("mw-head-base").style.backgroundColor = "yellow";
  }
