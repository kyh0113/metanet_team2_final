// common.js

// 비동기적으로 사이드바를 가져와서 mainContent에 적용하는 함수
function loadSidebar() {
	console.log('Trying to load sidebar...');
	console.log('Document ready state:', document.readyState);

	// mainContent 엘리먼트 확인
	const mainContent = document.getElementById('mainContent');
	console.log('#mainContent:', mainContent); // null이 출력됨

	// DOMContentLoaded 이벤트가 발생할 때까지 대기
	document.addEventListener('DOMContentLoaded', function() { // 아예 실행 안돼
		console.log("이거 실행되나"); // 실행 안돼

		// mainContent가 있는 경우에만 사이드바를 로드
		if (mainContent) {
			// Fetch를 사용하여 sidebar.html을 가져옴
			fetch('/include/sidebar.html')
				.then(response => {
					if (!response.ok) {
						throw new Error('Network response was not ok');
					}
					return response.text();
				})
				.then(data => {
					// mainContent에 가져온 sidebar.html 적용
					console.log('Data:', data);
					console.log('Fetch success!'); // 추가
					mainContent.innerHTML = data;

					// 가져온 스크립트를 실행 (만약 스크립트가 있다면)
					const scripts = mainContent.getElementsByTagName('script');
					for (let script of scripts) {
						eval(script.innerText);
					}
				})
				.catch(error => console.error('Error:', error));
		} else {
			console.error('#mainContent not found');
		}
	});
}

// DOMContentLoaded 이벤트가 발생하면 loadSidebar 함수 호출
document.addEventListener('DOMContentLoaded', loadSidebar);