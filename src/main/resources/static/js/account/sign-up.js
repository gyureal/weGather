
const form = document.getElementById('form');
const errorTexts = document.querySelectorAll('.form-text.text-danger');

const errorTextsHide = () => {
  errorTexts.forEach((errorText) => {
    errorText.style.display = 'none';
  });
}

form.addEventListener('submit', (event) => {
  event.preventDefault(); // 기본 form submit 이벤트 중단

  const formData = new FormData(event.target);
  let member = {};
  for (const entry of formData.entries()) {
    const [key, value] = entry;
    member[key] = value;
  }
  errorTextsHide();
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
    console.log(error);
    const errorDetails = error.response.data.errorDetails;

    showValidationResult(errorTexts, errorDetails);
  });
};

const showValidationResult = (errorTexts, errorDetails) => {
  errorDetails.forEach((errorDetail) => {
    errorTexts.forEach((errorText) => {
      const fieldName = errorText.getAttribute('data-field');
      if (errorDetail.field === fieldName) {
        errorText.textContent = errorDetail.reason;
        errorText.style.display = 'block';
      }
    })
  })
}
