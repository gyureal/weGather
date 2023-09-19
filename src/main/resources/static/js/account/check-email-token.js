const checkFail = document.getElementById("check-fail");
const checkSuccess = document.getElementById("check-success");

const pageLoad = () => {
  const email = document.getElementById('email-data').getAttribute('data-email');
  const token = document.getElementById('token-data').getAttribute('data-token');
  checkEmailToken(email, token);
}

window.onload = pageLoad;

const checkEmailToken = (email, token) => {
  axios.request({
    url: '/check-email-token',
    method: 'POST',
    params: {
      email : email,
      token : token
    }
  }).then((response) => {
    const usernameTag = document.getElementById('username');
    usernameTag.textContent = response.data;
    checkSuccess.style.display = 'block';
  }).catch((error) => {
    console.log(error);
    checkFail.style.display = 'block';
  });
};
