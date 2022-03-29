class ProfileComponent
{
    /**
     * @param {User} user
     */
    static drawProfileHead(user)
    {
        let wrapperCard = document.createElement('div');
        wrapperCard.classList.add('card', 'mb-3', 'profile-card');
        wrapperCard.style.width = '90%';

        let headerImage = document.createElement('img');
        // TODO: Possibility to use a custom individual image
        headerImage.src = 'static/image/tim-hart-MMEryLDqkXY-unsplash.jpg';
        headerImage.classList.add('card-img-top');
        wrapperCard.appendChild(headerImage);

        let centerProfile = document.createElement('div');
        centerProfile.classList.add('user', 'text-center');
        let profile = document.createElement('div');
        profile.classList.add('profile');
        let profileImage = document.createElement('img');
        profileImage.src = UserData.getProfileImage();
        profileImage.classList.add('rounded-circle');
        profileImage.width = 80;
        profile.appendChild(profileImage);
        centerProfile.appendChild(profile);

        let profileBody = document.createElement('div');
        profileBody.classList.add('mt-5', 'text-center');
        let profileHeadline = document.createElement('h4');
        profileHeadline.classList.add('mb-0');
        profileHeadline.textContent    = '@' + user.username;
        let profileHeadlineSmall       = document.createElement('span');
        profileHeadlineSmall.innerText = 'username -> real name to be followed';
        profileHeadlineSmall.classList.add('text-muted', 'd-block', 'mb-2');
        let buttonFollow = document.createElement('button');
        buttonFollow.classList.add('btn', 'btn-primary', 'btn-sm', 'follow');
        buttonFollow.innerText = 'Follow';
        let profileContent = document.createElement('div');
        profileContent.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mt-4', 'px-4');
        profileContent.appendChild(this.#createStats('Followers', user.followerCount));
        profileContent.appendChild(this.#createStats('Follows', user.followsCount));
        profileContent.appendChild(this.#createStats('Created', user.created));

        profileBody.appendChild(profileHeadline);
        profileBody.appendChild(profileHeadlineSmall);
        if (UserData.getUser().id !== user.id) {
            profileBody.appendChild(buttonFollow);
        }
        profileBody.appendChild(profileContent);

        wrapperCard.appendChild(profileBody);

        return wrapperCard;
    }

    static #createStats(title, value)
    {
        let stats = document.createElement('div');
        stats.classList.add('stats');
        let statsTitle = document.createElement('h6');
        statsTitle.classList.add('mb-0');
        statsTitle.innerText = title;
        let statsValue = document.createElement('span');
        statsValue.innerText = value;

        stats.appendChild(statsTitle);
        stats.appendChild(statsValue);

        return stats;
    }
}