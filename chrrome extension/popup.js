// Initialize button with user's preferred color
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

    document.getElementById("content").style.backgroundColor = "blue";
    document.getElementById("mw-head-base").style.backgroundColor = "yellow";
  }