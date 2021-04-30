local date = ARGV[1]
local s = tonumber(ARGV[2])
local e = tonumber(ARGV[3])
local p = tonumber(ARGV[4]) -- p是一个1到1000之内的数字，代表生成1的概率有多大，由外部传入

for i=s,e,1 do
    math.randomseed(redis.call('TIME')[2])
    local random = math.random(100000) % 1000

    if random <= p then
        redis.call('PFADD',date,i)
    end
end