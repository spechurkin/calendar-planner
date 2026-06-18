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

document.querySelectorAll('.calendar-day').forEach(day => {
    day.addEventListener('click', async () => {
        if (!isProjectMember || !currentUserId) {
            console.warn('Toggle not allowed');
            return;
        }

        const date = day.dataset.date;
        console.log(`🔄 Toggling: ${date}`);

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';

            const formData = new FormData();
            formData.append('projectId', projectId);
            formData.append('userId', currentUserId);
            formData.append('date', date);

            const response = await fetch('/api/availability/toggle', {
                method: 'POST',
                headers: {
                    'X-XSRF-TOKEN': csrfToken
                },
                body: formData,
                credentials: 'same-origin'
            });

            if (response.ok) {
                console.log('✅ Toggle successful');
            } else {
                console.error('❌ Failed:', response.status, await response.text());
            }
        } catch (err) {
            console.error('❌ Error:', err);
        }

        setTimeout(() => location.reload(), 400);
    });
});

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
