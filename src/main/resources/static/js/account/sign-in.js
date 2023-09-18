const form = document.getElementById('form');
const errorText = document.getElementById('error-text');

const errorTextHide = () => {
  errorText.style.display = 'none';
}

const errorTextShow = () => {
  errorText.style.display = 'block';
}

form.addEventListener('submit', (event) => {
  event.preventDefault();

  const formData = new FormData(form);
  let account = {
    usernameOrEmail : formData.get("usernameOrEmail"),
    password : formData.get("password")
  };

  errorTextHide();
  debugger;
  signIn(account);
});

const signIn = (account) => {
  axios.request({
    url: '/sign-in',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json;charset=utf-8',
    },
    data: account
  }).then((response) => {
    alert('성공');
    window.location.href = '/';
  }).catch((error) => {
    errorTextShow();
    console.log(error);
  })
}
