// if we're sent this file, redirect to login
console.log(window.location.href);
if(window.location.href.substring(window.location.href.length - 6) !== '/login') {
    window.location.href = "/login";
}

function sendData(path, username, password_hash, salt) {
    const dict = [];
    dict.push(`username=${encodeURI(username)}`);
    dict.push(`password_hash=${encodeURI(password_hash)}`);
    if(salt !== null) {
        dict.push(`salt=${encodeURI(salt)}`);
    }
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
    request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    request.send(data);
}

function getSalt() {
    return new Promise(function(resolve, reject) {
        const request = new XMLHttpRequest();
        request.open('GET', 'getsalt');
        request.onload = function() {
            if (request.status === 200) {
                resolve(request.response);
            } else {
                reject(request.status);
            }
        };
        request.send();
    });
};

function getSaltFromUsername(username) {
    return new Promise(function(resolve, reject) {
        const request = new XMLHttpRequest();
        request.open('GET', encodeURI(`getsaltfromuser?username=${username}`));
        request.onload = function() {
            if (request.status === 200) {
                resolve(request.response);
            } else {
                reject(request.status);
            }
        };
        request.send();
    });
};

const sha256sum = async (input) => {
    const buffer = new TextEncoder().encode(input);
    const hashBuffer = await window.crypto.subtle.digest("SHA-256", buffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const retval = hashArray.map((i) => i.toString(16).padStart(2, "0")).join("");
    return retval;
};

const login_button = document.getElementById('LoginButton');
if(login_button !== null) {
    login_button.addEventListener('click', async () => {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        // get salt
        const salt = await getSaltFromUsername(username);
        console.log("salt: ", salt);

        const hash = await sha256sum(password + salt);

        sendData("login", username, hash);
    });
}

const register_button = document.getElementById('RegisterButton');
if(register_button !== null) {
    register_button.addEventListener('click', async () => {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        // get salt
        const salt = await getSalt();
        console.log("salt: ", salt);

        const hash = await sha256sum(password + salt);

        sendData("register", username, hash, salt, true);
    });
}