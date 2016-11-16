/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//window.onload = init;
//window.onload = init;
var socket = new WebSocket("ws://localhost:8080/ChatBox/login");
socket.onmessage = onMessage;

function onMessage(event) {
    var device = JSON.parse(event.data);
    if (device.action === "loginResponse") {
            var status = device.status;
            if(status === 1) {//invalid username
                window.alert("Invalid username");
            }else if(status === 2) {//invalid password
                window.alert("Invalid password");
            }else if(status === 4) {//Successful Registration!
                window.alert("Successfully registered!");
            }else if(status === 5) {//Unsuccessful Registration!
                window.alert("Problem registering user");
            }else{//Successful login!
                socket.close();
                var name = document.getElementById('username').value;
                localStorage.setItem("username", name);
                //window.location.href = 'chat.html';
                window.location.assign("chat.html")
                //var openedWindow = window.open("chat.html");
                //openedWindow.login();
            }
    }
    if (device.action === "registrationResponse") {
        addMessage(device.message);
        //device.parentNode.removeChild(device);
    }
    
}


// Sending a login request to the server!
function login() {
    var name = document.getElementById('username').value;
    if(name === "") {
    window.alert("Please provide a username");
    }else{
        var password = document.getElementById('password').value;
        var DeviceAction = {
            action: "login",
            username: name,
            password: password
        };
    socket.send(JSON.stringify(DeviceAction));
    }
}