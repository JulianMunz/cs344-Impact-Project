
let colorDivs = document.getElementById("colorDivs");

colorDivs.addEventListener("click", async () => {
  Hide();
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
      for (var i = 0, max=response.length; i < max; i++) {
        var elements = document.getElementsByClassName(response[i]);
        i++;
        for (var k = 0, maximum = elements.length; k < maximum; k++) {
          console.log(response[i]);
          var colour = "#81F3F4"
          if (maximum > 1) {
            if (response[i] == "1") {
              colour = "#81F3F4";
              break;
            }
          }
          if (response[i] == "1") {
            colour = "#81F3F4";
          } else if (response[i] == "2") {
            colour = "#F4CA81";
          } else if (response[i] == "3") {
            colour = "#72E574";
          } else if (response[i] == "4") {
            colour = "#CE2E3E";
          } else if (response[i] == "5") {
            colour = "#9E2CCE";
          }
          elements[k].style.backgroundColor = colour;
          // document.getElementById("list").innerHTML += 
          //   "<li>" + response[i] + "</li>";
        }
      }

      var footers = document.getElementsByTagName('footer');
      for (i = 0; i < footers.length; i++) {
        footers[i].style.backgroundColor = "#F4CA81";
      }
      console.log(response.length);
    });

    // document.getElementById("content").style.backgroundColor = "blue";
    // document.getElementById("mw-head-base").style.backgroundColor = "yellow";
  }

  function Hide() {
    document.getElementById("list").innerHTML += 
        "<li>Adele</li>";
    document.getElementById("list").innerHTML += 
        "<li>bdele</li>";
    document.getElementById("list").innerHTML += 
        "<li>Ab</li>";
    document.getElementById("paragraph").style.display = "none";
    document.getElementById("colorDivs").style.display = "none";
  }
