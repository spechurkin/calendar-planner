const projectId =
    document.body.dataset.projectId;

const currentUserId =
    document.body.dataset.currentUserId;

const isProjectMember =
    document.body.dataset.isProjectMember === 'true';

const selectedUserStorageKey =
    `selectedUserId:${projectId}`;

let selectedUserId =
    localStorage.getItem(selectedUserStorageKey);

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
}

const users =
    document.querySelectorAll(
        '.selectable-user'
    );

users.forEach(user => {
    user.addEventListener('click', (event) => {
        if (event.target.closest('form, a, button')) {
            return;
        }

        if (user.dataset.userId !== currentUserId) {
            return;
        }

        users.forEach(u =>
            u.classList.remove('active-user')
        );
        user.classList.add('active-user');
        selectedUserId = user.dataset.userId;
        localStorage.setItem(
            selectedUserStorageKey,
            selectedUserId
        );
    });
});

if (isProjectMember) {
    document
        .querySelectorAll('.calendar-day')
        .forEach(day => {
            day.addEventListener(
                'click',
                async () => {
                    selectedUserId = currentUserId;

                    if (!selectedUserId) {
                        return;
                    }

                    const date =
                        day.dataset.date;

                    await fetch(
                        `/api/availability/toggle?projectId=${projectId}&userId=${selectedUserId}&date=${date}`,
                        {
                            method: 'POST',
                            headers: {
                                'X-XSRF-TOKEN': getCsrfToken()
                            }
                        }
                    );

                    location.reload();
                }
            );
        });
}

const existingIds =
    Array.from(document.querySelectorAll(".selectable-user"))
        .map(u => u.dataset.userId);

const stored =
    localStorage.getItem(selectedUserStorageKey);

if (!existingIds.includes(stored)) {
    localStorage.removeItem(selectedUserStorageKey);
    selectedUserId = null;
}

if (currentUserId && existingIds.includes(currentUserId)) {
    selectedUserId = currentUserId;
    localStorage.setItem(selectedUserStorageKey, currentUserId);
} else {
    selectedUserId = null;
    localStorage.removeItem(selectedUserStorageKey);
}

if (selectedUserId) {
    const active =
        document.querySelector(
            `.selectable-user[data-user-id="${selectedUserId}"]`
        );
    if (active) {
        active.classList.add("active-user");
    }
}
