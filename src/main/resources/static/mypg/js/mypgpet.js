/*추가 하기 버튼 유뮤 코드*/ 
$('document').ready(function(){
    let petlist=$('.pet-detail-area');
    let list=petlist.length;
    if(list >= 3){
        $('.addbtn').css('display','none');
    }else{
        $('.addbtn').css('display','block');
    }

    let petId =$('.x-box').data('petid');

    let userId = $('.petmain').data('userid');


})

/* 날짜 및 나이 계산 코드*/
$('document').ready(function(){
let petdetailarea =$('.pet-detail-area');
for(let i =0; i<petdetailarea.length; i++){
    // i번째 기입된 날짜를 불러와서 저장
    let pet = $('.pet-brith',petdetailarea[i]);

// 날짜 자동으로 계사해주는 쿼리 
let petdateText = pet.text().trim(); // trim()은 문자열 양 끝의 공백을 제거합니다.

// 정규식을 사용하여 'YYYY-MM-DD' 형식의 날짜를 추출
let petDateMatch = petdateText.match(/\d{4}-\d{2}-\d{2}/);


// petDateMatch가 유효한지 확인 후 moment 객체로 변환
if (petDateMatch) {
    let petbrith = moment(petDateMatch[0]);

    // 현재 날짜 객체
    let currentDate = moment();

    let yearsDifference = currentDate.diff(petbrith, 'years');
    let monthsDifference = currentDate.diff(petbrith, 'months') % 12;


    // 날짜 차이 계산
    let ageDifference;

    if (yearsDifference === 0) {
        ageDifference = '(' +`${monthsDifference}개월`+')';

   
    } else {
        ageDifference = '(' +`${yearsDifference}년 ${monthsDifference}개월`+')';
      
    }
    $('.pet-age', petdetailarea[i]).text(ageDifference);
} else {
    console.log('날짜 형식이 유효하지 않습니다.');
}

}
})
// 수정하기 위해서 칸을 클릭시 쿼리스크링으로 같이 이동하는 스크립트
$('.pet-detail-info').on('click',function(){
    let petId = $(this).data('petnum');
    let userId = $('.petmain').data('userid');
    window.location.href=`/mypg/petupdate/${petId}?userId=${userId}`;


})



//x버튼 클릭시 데이터 삭제 
$('.x-box span').on('click',function(){
    let userId = $('.petmain').data('userid');
    let petId = $(this).data('petid');

    if(confirm("삭제 하시겠습니까?")) {
        $.ajax({

            url: '/mypgs/remove/' + petId,
            type: 'post',
            data: JSON.stringify({
                petId: petId
            }),
            contentType: 'application/json; charset=utf-8',

            success: function () {

                alert("삭제가 완료되었습니다.");
                window.location.href = `/mypg/mypet/${userId}`;
            }, error: function (a, b, c) {
                console.error(c);
                alert("삭제중 오류가 발생하였습니다.")
            }
        })
    }else{
        alert("삭제가 취소 되었습니다.");
        window.location.href = `/mypg/mypet/${userId}`;

    }


})





