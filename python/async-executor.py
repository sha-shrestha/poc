import asyncio
import itertools
import threading
from random import randint


class SafeCounter(object):
    def __init__(self):
        self._number_of_read = 0
        self._counter = itertools.count()
        self._read_lock = threading.Lock()

    def increment(self):
        next(self._counter)

    def value(self):
        with self._read_lock:
            value = next(self._counter) - self._number_of_read
            self._number_of_read += 1
        return value


counter = SafeCounter()
error_counter = SafeCounter()
errors_indices = []

max_parallel_executions = 1000

work_count = 1000
work_items = range(work_count)

# Check status upon 1% completion
status_check_count = int(round(work_count/20))

sem = asyncio.Semaphore(max_parallel_executions)


async def execute(code):
    wait_time = randint(1, 5)
    exit_code = randint(0, 1)
    await asyncio.sleep(wait_time)  # I/O, context will switch to main function
    counter.increment()
    val = counter.value()
    if val % status_check_count == 0 or val == work_count:
        print('Completed %s' % (counter.value()))
        print('Errors %s' % (error_counter.value()))

    if exit_code == 1:
        errors_indices.append(code)
        error_counter.increment()


async def safe_execute(i):
    async with sem:  # semaphore limits num of simultaneous executions
        return await execute(i)


async def main(work_items):
    tasks = [asyncio.ensure_future(safe_execute(i)) for i in work_items]
    await asyncio.gather(*tasks)

if __name__ == '__main__':
    loop = asyncio.get_event_loop()

    try:
        loop.run_until_complete(main(work_items))
    finally:
        loop.close()

    print(errors_indices)
