
let colorDivs = document.getElementById("colorDivs");
// var globalwind = { labels: [], myResponse: new Response() };
//window.labels = [];
//globalResponse = null;
//window.myResponse = new Response();

colorDivs.addEventListener("click", async () => {
  Hide();
    let [tab] = await chrome.tabs.query({ active: true, currentWindow: true });
    chrome.scripting.executeScript({
      target: { tabId: tab.id },
      function: showColoring,
    });
  PopulateList();
  });

  // The body of this function will be executed as a content script inside the
  // current page
  function showColoring() {
    var text = "true";
    chrome.runtime.sendMessage({run: true, data:{text}, url: window.location.href}, function(response) {
      console.log(response);
      //globalwind.myResponse = response;
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
            //globalwind.labels.push("hi");
            //document.getElementById("list").innerHTML +=  "<li>1</li>";
          } else if (response[i] == "2") {
            colour = "#F4CA81";
            //globalwind.labels.push("hey");
            //document.getElementById("list").innerHTML +=  "<li>2</li>";
          } else if (response[i] == "3") {
            colour = "#72E574";
            //globalwind.labels.push("hey");
            //document.getElementById("list").innerHTML +=  "<li>3</li>";
          } else if (response[i] == "4") {
            colour = "#CE2E3E";
            //globalwind.labels.push("hey");
            //labels.push("4");
            //document.getElementById("list").innerHTML +=  "<li>4</li>";
          } else if (response[i] == "5") {
            colour = "#9E2CCE";
            //globalwind.labels.push("hey");
            //labels.push("5");
            //document.getElementById("list").innerHTML +=  "<li>5</li>";
          } else if (response[i] == "6") {
            colour = "#DEB8BE";
            //globalwind.labels.push("hey");
            //labels.push("6");
            //document.getElementById("list").innerHTML +=  "<li>6</li>";
          }
          elements[k].style.backgroundColor = colour;
        }
      }

      var footers = document.getElementsByTagName('footer');
      for (i = 0; i < footers.length; i++) {
        var children = footers[i].childNodes;
        for (j = 0; j < children.length; j++) {
          try {
            children[j].style.backgroundColor = "#F4CA81";
          } catch(err) {

          }
        }
        footers[i].style.backgroundColor = "#F4CA81";
      }
      console.log(response.length);
    });
    return new Promise(resolve => {
      setTimeout(() => {
        resolve('resolved');
      }, 12000);
    });
  }

  function Hide() {
    document.getElementById("loadingdiv").innerHTML += 
        "<p id=loading.. > Loading...</p>";
     document.getElementById("loadingdiv").innerHTML += 
        "<div id=loading class=\"loader\"></div>";
    // document.getElementById("list").innerHTML += 
    //     "<li>bdele</li>";
    // document.getElementById("list").innerHTML += 
    //     "<li>Ab</li>";
    document.getElementById("paragraph").style.display = "none";
    document.getElementById("colorDivs").style.display = "none";
  }

  async function PopulateList(){
    const result = await showColoring();
    document.getElementById("loadingdiv").style.display = "none";
    document.getElementById("list").innerHTML += 
      "<li id=main>Main</li>";
    document.getElementById("list").innerHTML += 
      "<li id=footer>Footer</li>";
    document.getElementById("list").innerHTML += 
        "<li id=header>Header</li>";
    document.getElementById("list").innerHTML += 
        "<li id=sidebar>Sidebar</li>";
    document.getElementById("list").innerHTML += 
        "<li id=disclaimer>Disclaimer</li>";
    document.getElementById("list").innerHTML += 
        "<li id=comments>Comments</li>";
  }

  function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
  }
  
