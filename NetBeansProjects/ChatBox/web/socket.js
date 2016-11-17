
var socket = socket = new WebSocket("ws://192.168.2.13:8080/ChatBox/actions");
socket.onmessage = onMessage;
var name = localStorage.getItem("username");
window.onload = login;
//Receiving request from chat server
function onMessage(event) {
    var device = JSON.parse(event.data);
    if (device.action === "login") {
          document.getElementById('test_text').innerHTML = device.user+' logged in';
          window.alert("user logged in!");
    }
    if (device.action === "message") {
        addMessage(device.message);
        //device.parentNode.removeChild(device);
    }
        if (device.action === "history") {
        if(device.first === 1) {
            Clear();
        }
        historyMessage(device.message);
        //addMessage(device.message);
        //device.parentNode.removeChild(device);
    }
    if (device.action === "userList") {
        updateUsers(device.size, device.names);
        //device.parentNode.removeChild(device);
    }
        if (device.action === "loggedIn") {
        loginNotification(device.user);
        //device.parentNode.removeChild(device);
    }
        if (device.action === "loggedOut") {
        logoutNotification(device.user);
        //device.parentNode.removeChild(device);
    }
    if (device.action === "logout") {
        //var node = document.getElementById(device.id);
        //var statusText = node.children[2];
    }
    
}
function updateUsers(size, names) {
    var users = names.split(" ");
    var li;
    var i = 0;
  document.getElementById("usersOnline").innerHTML = "Users online: "+size;
  var ul = document.getElementById("userList");
  if (ul) {
    while (ul.firstChild) {
      ul.removeChild(ul.firstChild);
    }
  }
  for(i = 0; i < size; i++) {
    li = document.createElement("li");
    li.appendChild(document.createTextNode(users[i]));
    ul.appendChild(li);
  }  
}
function Logout() {
    window.location.href="index.html";
}
function loginNotification(user) {
    addMessage(user+" has logged in");
}
function logoutNotification(user) {
    addMessage(user+" has logged out");
}
function historyMessage(input) {
    addMessage(input);
}
function addMessage(message) {
  var ul = document.getElementById("message_list");
  var li = document.createElement("li");
  li.appendChild(document.createTextNode(message));
  ul.appendChild(li);
      body = document.getElementById("messageBody");
    body.scrollTop = body.scrollHeight;
}
function checkMessage(evt) {
    var userInput = document.getElementById("user_input");
    if (evt.keyCode === 13 && userInput.value.length > 0) {
        sendMessage();
    }
}
function sendMessage() {
    //User is requesting to send message
    var message = document.getElementById("user_input");
    if(message.value === "") {
        return;
    }else{
        var DeviceAction = {
            action: "message",
            name: name,
            message: message.value
        };
    socket.send(JSON.stringify(DeviceAction));
    message.value = "";
    message.focus();
    };
}
    //Clear our message list.
    function Clear() {
        document.getElementById("message_list").innerHTML = "";
    }
    //Retrieve user message history
    function History() {
    var DeviceAction = {
        action: "history",
    };
    socket.send(JSON.stringify(DeviceAction));
    }
    function login() {
    name = localStorage.getItem("username");
        document.getElementById("nameTag").innerHTML = name;
        window.alert("login packet sent");
    var DeviceAction = {
        action: "enter",
        name: name
    };
    socket.send(JSON.stringify(DeviceAction));
    }
    //window.addEventListener("load", login, false);