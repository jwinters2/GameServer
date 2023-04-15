function sendData(path, username, password) {
    const dict = [];
    dict.push(`username=${encodeURI(username)}`);
    dict.push(`password=${encodeURI(password)}`);
    const data = dict.join('&');
    
    const request = new XMLHttpRequest();
   
    request.onreadystatechange = function() {
        if (this.readyState === 4) {
            if(this.status === 200) {
                // successful response means we can login
                window.location.href = this.responseText;
            } else {
                // some sort of error: just display it
                const alert = document.getElementById("responseAlert");
                alert.innerHTML = this.responseText;
                alert.style.visibility = "visible";
            }
        }
    };

    request.open('POST', path);
    request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	request.setRequestHeader(getCsrfTokenHeader(), getCsrfToken());

    request.send(data);
}

function getCsrfTokenHeader() {
	return document.querySelector("meta[name='_csrf_header']").content;
}

function getCsrfToken() {
	return document.querySelector("meta[name='_csrf']").content;
}

const login_button = document.getElementById('LoginButton');
if(login_button !== null) {
    login_button.addEventListener('click', async () => {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        sendData("login", username, password);
    });
}

const register_button = document.getElementById('RegisterButton');
if(register_button !== null) {
    register_button.addEventListener('click', async () => {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        sendData("register", username, password);
    });
}