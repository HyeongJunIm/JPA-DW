//파일 추가 코드
let $input = $('#post-image');
let $img = $('.img-area');

// file change이벤트로 미리보기 갱신하기
$input.on('change', function () {
    let files = this.files;

    // 길이 체크함수 (files, 원하는 길이)
    let newFiles = checkLength(files, 1);
    updateImg(newFiles);
});

// 클릭 이벤트로 이미지 지우고 미리보기 갱신하기
$img.on('click', function (e) {
    let name = $(e.target).data('name');

    removeImg(name);
    updateImg($input[0].files);
});

// //미리보기 삭제 함수
function removeImg(name) {
    let dt = new DataTransfer();

    let files = $input[0].files;

    for (let i = 0; i < files.length; i++) {
        if (files[i].name !== name) {
            dt.items.add(files[i]);
        }
    }
    $input[0].files = dt.files;
}

// //파일 길이 체크 함수(체크할 files객체, 제한할 길이)
function checkLength(files, num) {
    if (files.length > num) {
        alert(`파일은 ${num}개까지만 첨부 가능합니다.`);
        // 최대 수를 넘으면 비어있는 files객체 반환
        return new DataTransfer().files;
    }
    return files;
}


// // 이미지 수가 1개보다 적으면 기본이미지로 대체함
function updateImg(files) {
    for (let i = 0; i <= 1; i++) {

        if (i <= files.length) {
            let src = URL.createObjectURL(files[i]);
            $('.img').eq(i).attr('src', src).data('name', `${files[i].name}`);

            
        } else {
            $('.img')
                .eq(i)
                .attr('src',"../img/b556fdf429d8de25c3acf62f8186ddb9.png")
                .data('name', null);
            
        }
    }
}

/**************************************************/ 

/* 닉네임 유효성검사 스크립트 */
function checkPetNameFormat() {


    $('.inp-name').keyup(function () {
        let nick = $(".inp-name").val();
        let reg =/^[가-힣a-zA-Z0-9]{1,10}$/;

        let nickCheck = reg.test(nick);


        if(nickCheck) {
            $('.nonename').css("display", "none");
        } else {
            $('.nonename').css("display", "block");
        }

    });
}

/*날짜 기입 자동 - 생성*/ 
$('.inp-birth').on('input',function(){
    let inputDay = $(this).val().replace(/-/g,'');
    let reg = /^\d{8}$/;
    if(reg.test(inputDay)){
            inputDay = inputDay.slice(0,4) + '-' + inputDay.slice(4,6) +  '-' + inputDay.slice(6, 8);
        }else {
            // 잘못된 입력이면 값을 지우고 다시 작성할 수 있도록 처리
            $(this).val('');
          }

    $(this).val(inputDay);
})

/*날짜 기입 유효성 검사 */ 
$('.inp-birth').on('keyup',function () {
    let day = $(".inp-birth").val();
    let reg =/^(19|20)\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/;

    let dayCheck = reg.test(day);
    

    if(dayCheck) {
        $('.noneday').css("display", "none");
    } else {
        $('.noneday').css("display", "block");
    }

});

// 반려동물 이름 중복 검사
function checkPetName() {
    $('#petName').change(function () {
        let petName = $('#petName').val();
        let userId = $('.userid').val();
        $.ajax({

            url: '/mypgs/name/check',
            type: 'post',
            data: {
                name: petName,
                userId : userId
            },
            success: function(result) {
                console.log(petName);
                console.log(userId);
                if (result) {
                    $('.petName-unavailable').css('display','none');
                    $('.petName-available').css('display','block')
                } else {
                    $('.petName-unavailable').css('display','block');
                    $('.petName-available').css('display','none')
                }
            }, error: function (a, b, c) {
                console.error(c);
            }
        })
    })
}



$('document').ready(function(){
    checkPetNameFormat();
    checkPetName();

    let genderMen =$('#gendertype-m');
    let genderWomen =$('#gendertype-f');
    let gendercheck = $('.gender-box');
    let neuterings=$('#neuter');
    let neuterBox = $('.neuter-box');
    if(genderMen.is(':checked')){
        gendercheck.eq(0).addClass('checked');
    }else if(genderWomen.is(':checked')){
        gendercheck.eq(1).addClass('checked');
    }

    if(neuterings.is(':checked')){
        neuterBox.eq(0).addClass('checked');
    }
})