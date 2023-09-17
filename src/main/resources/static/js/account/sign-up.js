
const form = document.getElementById('form');
const errorTexts = document.querySelectorAll('.form-text.text-danger');

const pageLoad = () => {
  errorTextsHide();
}

const errorTextsHide = () => {
  errorTexts.forEach((errorText) => {
    errorText.style.display = 'none';
  });
}

window.onload = pageLoad;

form.addEventListener('submit', (event) => {
  event.preventDefault(); // 기본 form submit 이벤트 중단

  const formData = new FormData(event.target);
  let member = {};
  for (const entry of formData.entries()) {
    const [key, value] = entry;
    member[key] = value;
  }

  signUp(member);
})


const signUp = (member) => {
  axios.request({
    url: '/sign-up',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json;charset=utf-8',
    },
    data: member
  }).then((response) => {
    alert('성공');
    window.location.href = '/';
  }).catch((error) => {
    alert('실패');
    const errorData = error.response.data;

    console.error(error);
  });
};
