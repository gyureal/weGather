const emailText = document.getElementById('email-text');
const errorTag = document.getElementById('error-tag');

document.addEventListener('DOMContentLoaded', () => {
  getUserInfo((data) => {
    emailText.textContent = data.email;
  }, (error) => {
    console.log(error);
    errorTag.style.display = 'block';
  });
});
