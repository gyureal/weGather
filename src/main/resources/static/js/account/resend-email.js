const emailText = document.getElementById('email-text');
const errorTag = document.getElementById('error-tag');

document.addEventListener('DOMContentLoaded', () => {
  getUserInfo((data) => {
    emailText.textContent = data.email;
  }, (error) => {
    console.log(error);
  });
});


const resendButton = document.getElementById('resend-button');

resendButton.addEventListener('click', (event) => {
  event.preventDefault();
  resendConfirmEmail();
});

const resendConfirmEmail = () => {
  axios.request({
    url: '/resend-confirm-email',
    method: 'POST',
  }).then((response) => {
    alert('성공적으로 발송하였습니다.');
    window.location.href = '/';
  }).catch((error) => {
    console.log(error);
    errorTag.style.display = 'block';
  });
}
