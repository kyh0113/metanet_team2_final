// tokenHandler.js

// 토큰을 쿠키에 저장하는 함수
function setTokenInCookie(token) {
   console.log("setTokenInCookie");
    const expirationDate = new Date();
    expirationDate.setDate(expirationDate.getDate() + 7);
    document.cookie = `X-AUTH-TOKEN=${token}; expires=${expirationDate.toUTCString()}; path=/`;
    console.log(document.cookie);
}


// 쿠키에서 토큰을 가져오는 함수
function getTokenFromCookie() {
   console.log("fkfkfkffk");
   console.log(document.cookie);
    const cookies = document.cookie.split(';');
    for (const cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'X-AUTH-TOKEN') {
            return value;
        }
    }
    return null;
}
