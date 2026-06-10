let selectedUserId =
    localStorage.getItem("selectedUserId");

const users =
    document.querySelectorAll(
        '.selectable-user'
    );

users.forEach(user => {
    user.addEventListener('click', () => {
        users.forEach(u =>
            u.classList.remove('active-user')
        );
        user.classList.add('active-user');
        selectedUserId = user.dataset.userId;
        localStorage.setItem(
            "selectedUserId",
            selectedUserId
        );
    });
});

document
    .querySelectorAll('.calendar-day')
    .forEach(day => {

        day.addEventListener(
            'click',
            async () => {

                if (!selectedUserId) {
                    return;
                }

                const date =
                    day.dataset.date;

                await fetch(
                    `/api/availability/toggle?userId=${selectedUserId}&date=${date}`,
                    {
                        method: 'POST'
                    }
                );

                location.reload();
            }
        );
    });

const existingIds =
    Array.from(document.querySelectorAll(".selectable-user"))
        .map(u => u.dataset.userId);

const stored =
    localStorage.getItem("selectedUserId");

if (!existingIds.includes(stored)) {
    localStorage.removeItem("selectedUserId");
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
